import {Component, OnInit} from '@angular/core';
import {Guest} from '../../model/guest';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  guests: Guest[] = [new Guest('a', 'b', 'a', ['EGGS'], null)];

  constructor() {
  }

  ngOnInit() {
  }

}
