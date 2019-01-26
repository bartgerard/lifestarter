import {Component, OnInit} from '@angular/core';
import {Guest} from '../../model/guest';
import {RegistrationService} from '../../service/registration.service';
import {Registration} from '../../model/registration';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  guests: Guest[] = [new Guest()];

  constructor(
    private registrationService: RegistrationService
  ) {
  }

  ngOnInit() {
  }

  addGuest() {
    this.guests.push(new Guest());
  }

  register(
    registration: Registration
  ) {
    this.registrationService.register(registration);
  }

}
