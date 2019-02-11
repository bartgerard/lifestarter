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

  guestLimit = 2;

  contactInformation: ContactInformation;

  roles: string[] = [
    'guest.main',
    'guest.plus1',
    // 'guest.child1'
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
    this.guestLimit = 2;
  }

  addGuest(
    role: string,
    guest: Guest
  ) {
    this.guests.set(role, guest);
    this.step++;
  }

  skip(
    role: string
  ) {
    this.guests.delete(role);
    this.step++;
  }

  register() {
    this.registrationService.register(new Registration(
      this.contactInformation.email,
      this.guests,
      this.contactInformation
    ));
  }

}
