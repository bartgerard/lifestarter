import {Component, OnInit} from '@angular/core';
import {Country} from '../../model/country';
import {SelectItem} from 'primeng/api';
import {CountryService} from '../../service/country.service';

@Component({
  selector: 'app-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.css']
})
export class ContactInformationComponent implements OnInit {

  email: string;

  address: string;
  city: string;
  zipCode: string;
  selectedCountry: Country; // iso3

  phoneNumber: string;

  countries: SelectItem[];

  constructor(
    private countryService: CountryService
  ) {
  }

  ngOnInit() {
    this.countryService.countries()
      .subscribe(countries => this.countries = this.toCountryOptions(countries));
  }

  private toCountryOptions(
    countries: Country[]
  ) {
    return countries.map(country => {
      return {
        value: country,
        label: country.name + ' (' + country.iso3 + ')'
      };
    });
  }

}
