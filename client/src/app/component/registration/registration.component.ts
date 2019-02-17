import {Component, OnInit} from '@angular/core';
import {Guest} from '../../model/guest';
import {RegistrationService} from '../../service/registration.service';
import {Registration} from '../../model/registration';
import {ContactInformation} from '../../model/contact-information';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  step = -1;

  pledge: string;

  contactInformation: ContactInformation;

  roles: string[] = [
    'guest.main',
    'guest.plus1'
  ];

  guests: Map<string, Guest> = new Map<string, Guest>();

  constructor(
    private registrationService: RegistrationService
  ) {
  }

  ngOnInit() {
  }

  addContactInformation(
    contactInformation: ContactInformation
  ) {
    this.contactInformation = contactInformation;

    this.step = 0;
    window.scrollTo(0, 0);
  }

  addGuest(
    role: string,
    guest: Guest
  ) {
    this.registrationService.vipCheck(guest)
      .subscribe((roles) => {
        if (roles.length > 0) {
          this.roles = roles;
        }

        this.guests.set(role, guest);
        this.step++;
        window.scrollTo(0, 0);
      });
  }

  skip(
    role: string
  ) {
    this.guests.delete(role);
    this.step++;
  }

  register() {
    this.step++;
    window.scrollTo(0, 0);

    this.registrationService.register(new Registration(
      this.contactInformation.email,
      Array.from(this.guests.values()),
      [this.contactInformation],
      this.pledge,
    ))
      .subscribe(result => {
        console.log(result);
        this.step++;
        window.scrollTo(0, 0);
      });
  }

}
