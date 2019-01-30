import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DietService} from '../../service/diet.service';
import {SelectItem} from 'primeng/api';
import {TranslationService} from '../../service/translation.service';
import {AllergyService} from '../../service/allergy.service';
import {Guest} from '../../model/guest';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-guest',
  templateUrl: './guest.component.html',
  styleUrls: ['./guest.component.css']
})
export class GuestComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private dietService: DietService,
    private allergyService: AllergyService,
    private translationService: TranslationService
  ) {
  }

  @Input()
  title: string;

  @Input()
  index: number;

  @Input()
  guest: Guest;

  @Output() // ...Change is the keyword!
  guestChange = new EventEmitter<Guest>();

  dietaryOptions: SelectItem[];
  allergyOptions: SelectItem[];

  guestForm: FormGroup;

  allergies: string[] = [];

  toGuestFormGroup(
    guest: Guest
  ): FormGroup {
    return this.formBuilder.group({
      'firstName': [guest.firstName, Validators.required],
      'lastName': [guest.lastName, Validators.required],
      'diet': [guest.diet, Validators.required],
      'comment': [guest.comment]
    });
  }

  ngOnInit() {
    this.dietService.diets()
      .subscribe(diets => this.dietaryOptions = this.toDietaryOptions(diets));

    this.allergyService.allergies()
      .subscribe(allergies => this.allergyOptions = this.toAllergyOptions(allergies));

    if (!this.guest) {
      this.allergies = [];
      this.guestForm = this.formBuilder.group({
        'firstName': ['', Validators.required],
        'lastName': ['', Validators.required],
        'diet': ['', Validators.required],
        'comment': ['']
      });
    } else {
      this.allergies = this.guest.allergies;
      this.guestForm = this.toGuestFormGroup(this.guest);
    }
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

  saveGuest() {
    if (this.guestForm.valid) {
      const guest = new Guest(
        this.guestForm.value.firstName,
        this.guestForm.value.lastName,
        this.guestForm.value.diet,
        this.allergies,
        this.guestForm.value.comment,
      );

      console.log(`${guest.firstName} ${guest.lastName} ${guest.diet} - ${guest.comment} - ${guest.allergies}`);

      this.guestChange.emit(guest);
    }
  }

}
