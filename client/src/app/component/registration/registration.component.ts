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

  editContact = true;
  disableContact = true;
  hideGuests = false;

  guestLimit = 2;

  contactInformation: ContactInformation;
  guests: Guest[] = [new Guest()];

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

    this.guestLimit = 2;
    this.editContact = false;

    this.guests = [
      new Guest(),
      new Guest()
    ];
  }

  addGuest() {
    this.guests.push(new Guest());
  }

  register(
    registration: Registration
  ) {
    this.registrationService.register(new Registration(
      this.contactInformation.email,
      this.guests,
      this.contactInformation
    ));
  }

}
