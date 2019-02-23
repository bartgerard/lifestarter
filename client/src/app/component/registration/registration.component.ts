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

  step = -1;

  pledge: string;

  contactInformation: ContactInformation;

  roles: string[] = [
    'guest.main',
    'guest.plus1',
  ];

  guests: Map<string, Guest> = new Map<string, Guest>();

  selectedActivities: string[] = [];

  activities: string[] = [
    'CEREMONY',
    // 'RECEPTION',
    'PARTY',
  ];

  hideGuestsStep = true;
  hideConfirmationStep = true;
  hideProcessingStep = true;
  hideProcessedStep = true;

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

    this.next();
  }

  addGuest(
    role: string,
    guest: Guest
  ) {
    this.guests.set(role, guest);

    this.registrationService.vipCheck(guest)
      .subscribe(roles => {
        if (roles.length > 0 && this.roles.length < roles.length) {
          this.roles = roles;
        }

        this.next();
      });

    this.registrationService.vipActivityCheck(
      this.pledge,
      Array.from(this.guests.values())
    )
      .subscribe(activities => {
        // console.log(activities);
        this.activities = activities;
      });
  }

  skip(
    role: string
  ) {
    if (this.step === 0) {
      console.log('A girl has no name - Arya Stark');
    }

    this.guests.delete(role);

    this.next();
  }

  register() {
    this.next();

    this.registrationService.register(new Registration(
      this.contactInformation.email,
      Array.from(this.guests.values()),
      [this.contactInformation],
      this.pledge,
      this.selectedActivities,
    ))
      .subscribe(result => {
        // console.log(result);
        this.next();
      });
  }

  next() {
    this.step++;

    this.hideGuestsStep = this.step < 0 || this.roles.length < this.step;
    this.hideConfirmationStep = this.step !== this.roles.length;
    this.hideProcessingStep = this.step !== this.roles.length + 1;
    this.hideProcessedStep = this.step !== this.roles.length + 2;

    RegistrationComponent.toTop();
  }

}
