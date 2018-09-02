import {Component, OnInit} from '@angular/core';
import {DietService} from "../../service/diet.service";
import {SelectItem} from "primeng/api";

@Component({
  selector: 'life-guest',
  templateUrl: './guest.component.html',
  styleUrls: ['./guest.component.css']
})
export class GuestComponent implements OnInit {

  firstName: string;
  lastName: string;

  dietaryOptions: SelectItem[];

  constructor(
    private dietService: DietService
  ) {
  }

  ngOnInit() {
    this.dietService.diets()
      .subscribe(diets => this.dietaryOptions = [
        {label: 'A', value: 'A'}
      ]);
  }

  handleClick() {
    console.log(this.firstName + ' ' + this.lastName);
  }

}
