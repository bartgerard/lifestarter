import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Country} from '../../model/country';
import {SelectItem} from 'primeng/api';
import {CountryService} from '../../service/country.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ContactInformation} from '../../model/contact-information';

@Component({
  selector: 'app-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.css']
})
export class ContactInformationComponent implements OnInit {

  contactForm: FormGroup;

  countries: SelectItem[];

  @Output() // ...Change is the keyword!
  contactInformationChange = new EventEmitter<ContactInformation>();

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

    this.contactForm = this.formBuilder.group({
      'email': ['', Validators.required],
      'address': ['', Validators.required],
      'zipCode': ['', Validators.required],
      'city': ['', Validators.required],
      'country': ['', Validators.required],
      'phoneNumber': ['']
    });
  }

  addContactInformation() {
    if (this.contactForm.valid) {
      const contactInformation = new ContactInformation(
        this.contactForm.value.email,
        this.contactForm.value.address,
        this.contactForm.value.zipCode,
        this.contactForm.value.city,
        this.contactForm.value.country.iso3,
        this.contactForm.value.phoneNumber,
      );

      console.log(`${contactInformation.email} ${contactInformation.address} ${contactInformation.zipCode} ${contactInformation.countryIso3}`);

      this.contactInformationChange.emit(contactInformation);
    }
  }

}
