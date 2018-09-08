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
  comment: string;

  dietaryOptions: SelectItem[];

  constructor(
    private dietService: DietService
  ) {
  }

  ngOnInit() {
    this.dietService.diets()
      .subscribe(diets => this.dietaryOptions = GuestComponent.toDietaryOptions(diets));
  }

  private static toDietaryOptions(
    diets: string[]
  ) {
    return diets.map(diet => {
      return {
        label: diet,
        value: diet
      };
    });
  }

  handleClick() {
    console.log(this.firstName + ' ' + this.lastName + ' - ' + this.comment);
  }

}
