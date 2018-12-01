import {Component, OnInit} from '@angular/core';
import {Country} from '../../model/country';
import {SelectItem} from 'primeng/api';
import {CountryService} from '../../service/country.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.css']
})
export class ContactInformationComponent implements OnInit {

  userForm: FormGroup;

  countries: SelectItem[];

  constructor(
    private formBuilder: FormBuilder,
    private countryService: CountryService
  ) {
  }

  private static toCountryOptions(
    countries: Country[]
  ) {
    return countries.map(country => {
      return {
        value: country,
        label: country.name + ' (' + country.iso3 + ')'
      };
    });
  }

  ngOnInit() {
    this.countryService.countries()
      .subscribe(countries => this.countries = ContactInformationComponent.toCountryOptions(countries));

    this.userForm = this.formBuilder.group({
      'email': ['', Validators.required],
      'address': ['', Validators.required],
      'zipCode': ['', Validators.required],
      'city': ['', Validators.required],
      'country': ['', Validators.required],
      'phoneNumber': ['', Validators.required]
    });
  }

  saveUser() {
    console.log(this.userForm);
  }

}
