import {Component, OnInit} from '@angular/core';
import {RegistrationService} from '../../service/registration.service';
import {WaveService} from '../../service/wave.service';
import {Wave} from '../../model/wave';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  nbGuests = 0;

  constructor(
    private registrationService: RegistrationService
  ) {
  }

  ngOnInit() {
    this.registrationService.nbGuests()
      .subscribe(nbGuests => this.nbGuests = nbGuests);
  }

}
