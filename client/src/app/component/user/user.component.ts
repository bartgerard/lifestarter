import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  firstName: string;
  lastName: string;
  email: string;
  password: string;

  constructor() {
  }

  ngOnInit() {
  }

  handleClick() {
    console.log(this.firstName + ' ' + this.lastName);
  }

}
