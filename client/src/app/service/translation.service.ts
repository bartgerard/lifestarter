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
    .set('diet.FLEXITARIAN', 'Flexitariër')
    .set('common.next', 'Volgende')
    .set('common.add', 'Voeg toe')
    .set('common.confirm', 'Bevestig')
    .set('common.skip', 'Sla over')
    .set('common.select', 'Select')
    .set('common.selected', 'Selected')
    .set('common.none', 'none')
    .set('common.rsvp', 'RSVP')
    .set('common.reminder', 'Reminder')
    .set('common.includes', 'omvat')
    .set('guest.title', 'Guest')
    .set('guest.first-name', 'First Name')
    .set('guest.last-name', 'Last Name')
    .set('guest.diet', 'Dietary Preference')
    .set('guest.allergies', 'Allergies')
    .set('guest.comment', 'Comment')
    .set('guest.main', '(Hoofd)Genodigde')
    .set('guest.plus1', 'Tweede Genodigde of +1')
    .set('guest.child1', 'Kind')
    .set('contact.title', 'Contact Informatie')
    .set('contact.email', 'Email')
    .set('contact.address', 'Address')
    .set('contact.zip-code', 'Zip Code')
    .set('contact.city', 'City')
    .set('contact.country', 'Country')
    .set('contact.phone-number', 'Phone Number')
    .set('main.title', 'Togetherness Foreverness')
    .set('main.subtitle', 'Een registratie module voor vrienden en familie voor de trouw van Kim & Bart met een lichte knipoog naar een welbepaald crowdfunding platform.');

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
