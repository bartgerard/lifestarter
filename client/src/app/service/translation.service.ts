import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  map: Map<string, string> = new Map()
    .set('allergy.EGGS', 'Eggs')
    .set('allergy.MILK', 'Milk')
    .set('allergy.PEANUTS', 'Peanuts')
    .set('allergy.TREE_NUTS', 'Tree Nuts')
    .set('allergy.FISH', 'Fish')
    .set('allergy.SHELLFISH', 'Shellfish')
    .set('allergy.WHEAT', 'Wheat')
    .set('allergy.SOY', 'Soy')
    .set('allergy.PAPRIKA', 'Paprika')
    .set('allergy.PAPRIKA_POWDER', 'Paprika Powder')
    .set('diet.MEAT_EATER', 'Vleeseter')
    .set('diet.PESCATARIAN', 'Pescotariër')
    .set('diet.VEGETARIAN', 'Vegetariër')
    .set('diet.VEGAN', 'Veganist')
    .set('diet.FRUITARIAN', 'Fruitariër')
    .set('common.add', 'Add')
    .set('common.select', 'Select')
    .set('common.selected', 'Selected')
    .set('common.none', 'none')
    .set('guest.first-name', 'First Name')
    .set('guest.last-name', 'Last Name')
    .set('guest.diet', 'Dietary Preference')
    .set('guest.allergies', 'Allergies')
    .set('guest.comment', 'Comment')
    .set('contact.email', 'Email')
    .set('contact.address', 'Address')
    .set('contact.zip-code', 'Zip Code')
    .set('contact.city', 'City')
    .set('contact.country', 'Country')
    .set('contact.phone-number', 'Phone Number');

  constructor() {
  }

  public translate(
    label: string
  ): string {
    if (this.map.has(label)) {
      return this.map.get(label);
    }

    return label;
  }

}
