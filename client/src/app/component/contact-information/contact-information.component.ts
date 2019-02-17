import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Country} from '../../model/country';
import {SelectItem} from 'primeng/api';
import {CountryService} from '../../service/country.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ContactInformation} from '../../model/contact-information';
import {TranslationService} from '../../service/translation.service';

@Component({
  selector: 'app-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.css']
})
export class ContactInformationComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private countryService: CountryService,
    private translationService: TranslationService
  ) {
  }

  contactForm: FormGroup;

  countries: SelectItem[];

  contactMethods = this.toContactOptions([
      'NONE',
      'EMAIL',
      'PIGEON',
      'PHONE'
    ]
  );

  @Output() // ...Change is the keyword!
  contactInformationChange = new EventEmitter<ContactInformation>();

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

  private toContactOptions(
    contactOptions: string[]
  ) {
    return contactOptions.map(option => {
      return {
        value: option,
        label: this.translationService.translate('contact.method.' + option)
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
      'country': [''],
      'phoneNumber': [''],
      'contactMethod': ['', Validators.required]
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
        this.contactForm.value.contactMethod,
      );

      // console.log(`${contactInformation.email} ${contactInformation.address} ${contactInformation.zipCode}`);

      this.contactInformationChange.emit(contactInformation);
    }
  }

}
