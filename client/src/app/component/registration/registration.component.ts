import {Component, OnInit} from '@angular/core';
import {Guest} from '../../model/guest';
import {RegistrationService} from '../../service/registration.service';
import {Registration} from '../../model/registration';
import {ContactInformation} from '../../model/contact-information';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  constructor(
    private registrationService: RegistrationService,
    private route: ActivatedRoute
  ) {
  }

  step = 3;

  pledge: string;

  contactInformation: ContactInformation;

  roles: string[] = [
    'guest.main',
    'guest.plus1'
  ];

  guests: Map<string, Guest> = new Map<string, Guest>();

  private static toTop() {
    document.body.scrollTop = document.documentElement.scrollTop = 0;
  }

  ngOnInit() {
    this.route.queryParams
      .subscribe(params => {
        if (params.pledge) {
          this.pledge = params.pledge;
        }
      });
  }

  addContactInformation(
    contactInformation: ContactInformation
  ) {
    this.contactInformation = contactInformation;

    this.step = 0;
    RegistrationComponent.toTop();
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
        RegistrationComponent.toTop();
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
    RegistrationComponent.toTop();

    this.registrationService.register(new Registration(
      this.contactInformation.email,
      Array.from(this.guests.values()),
      [this.contactInformation],
      this.pledge,
    ))
      .subscribe(result => {
        console.log(result);
        this.step++;
        RegistrationComponent.toTop();
      });
  }

}
