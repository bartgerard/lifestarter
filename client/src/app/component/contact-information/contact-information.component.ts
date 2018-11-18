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

  constructor(
    private countryService: CountryService
  ) {
  }

  email: string;

  address: string;
  zipCode: string;
  city: string;
  selectedCountry: Country; // iso3

  phoneNumber: string;

  countries: SelectItem[];

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
  }

}
