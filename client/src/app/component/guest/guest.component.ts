import {Component, OnInit} from '@angular/core';
import {DietService} from '../../service/diet.service';
import {SelectItem} from 'primeng/api';
import {TranslationService} from '../../service/translation.service';
import {AllergyService} from '../../service/allergy.service';

@Component({
  selector: 'app-guest',
  templateUrl: './guest.component.html',
  styleUrls: ['./guest.component.css']
})
export class GuestComponent implements OnInit {

  firstName: string;
  lastName: string;
  selectedDiet: string;
  selectedAllergies: string[];
  comment: string;

  dietaryOptions: SelectItem[];
  allergyOptions: SelectItem[];

  constructor(
    private dietService: DietService,
    private allergyService: AllergyService,
    private translationService: TranslationService
  ) {
  }

  ngOnInit() {
    this.dietService.diets()
      .subscribe(diets => this.dietaryOptions = this.toDietaryOptions(diets));
    this.allergyService.allergies()
      .subscribe(allergies => this.allergyOptions = this.toAllergyOptions(allergies));
  }

  private toDietaryOptions(
    diets: string[]
  ) {
    return diets.map(diet => {
      return {
        value: diet,
        label: this.translationService.translate('diet.' + diet)
      };
    });
  }

  private toAllergyOptions(
    allergies: string[]
  ) {
    return allergies.map(allergy => {
      return {
        value: allergy,
        label: this.translationService.translate('allergy.' + allergy)
      };
    });
  }

  handleClick() {
    console.log(this.firstName + ' ' + this.lastName + this.selectedDiet + ' - ' + this.comment);
  }

}
