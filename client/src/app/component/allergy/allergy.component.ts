import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SelectItem} from 'primeng/api';
import {AllergyService} from '../../service/allergy.service';
import {TranslationService} from '../../service/translation.service';

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrls: ['./allergy.component.css']
})
export class AllergyComponent implements OnInit {

  @Input()
  allergies: string[];

  @Output() // ...Change is the keyword!
  allergiesChange = new EventEmitter<string[]>();

  allergyOptions: SelectItem[];

  constructor(
    private allergyService: AllergyService,
    private translationService: TranslationService
  ) {
  }

  ngOnInit() {
    this.allergyService.allergies()
      .subscribe(allergies => this.allergyOptions = this.toAllergyOptions(allergies));
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

}
