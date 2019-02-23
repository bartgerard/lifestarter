import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  map: Map<string, string> = new Map()
  // .set('allergy.EGGS', 'Eggs')
  // .set('allergy.MILK', 'Milk')
  // .set('allergy.PEANUTS', 'Peanuts')
  // .set('allergy.TREE_NUTS', 'Tree Nuts')
  // .set('allergy.FISH', 'Fish')
  // .set('allergy.SHELLFISH', 'Shellfish')
  // .set('allergy.WHEAT', 'Wheat')
  // .set('allergy.SOY', 'Soy')
  // .set('allergy.PAPRIKA', 'Paprika')
  // .set('allergy.PAPRIKA_POWDER', 'Paprika Powder')
  // .set('common.select', 'Select')
  // .set('common.selected', 'Selected')
  // .set('guest.title', 'Guest')
  // .set('guest.first-name', 'First Name')
  // .set('guest.last-name', 'Last Name')
  // .set('guest.diet', 'Dietary Preference')
  // .set('guest.allergies', 'Allergies')
  // .set('guest.comment', 'Comment')
    .set('allergy.EGGS', 'Eieren')
    .set('allergy.MILK', 'Melk')
    .set('allergy.PEANUTS', 'Pindanoten')
    .set('allergy.TREE_NUTS', 'Noten')
    .set('allergy.FISH', 'Vis')
    .set('allergy.SHELLFISH', 'Schaaldieren')
    .set('allergy.WHEAT', 'Graan')
    .set('allergy.SOY', 'Soja')
    .set('allergy.PAPRIKA', 'Paprika')
    .set('allergy.PAPRIKA_POWDER', 'Paprika Poeder')
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
    .set('common.select', 'Selecteer')
    .set('common.selected', 'Geselecteerd')
    .set('common.none', 'none')
    .set('common.rsvp', 'RSVP')
    .set('common.reminder', 'Reminder')
    .set('common.includes', 'omvat')
    .set('guest.title', 'Gast')
    .set('guest.first-name', 'Voornaam')
    .set('guest.last-name', 'Achternaam')
    .set('guest.diet', 'Voorkeuren')
    .set('guest.allergies', 'Allergieën')
    .set('guest.comment', 'Opmerkingen')
    .set('guest.main', '(Hoofd)Genodigde')
    .set('guest.plus1', 'Tweede Genodigde of +1')
    .set('guest.child1', 'Kind')
    .set('guest.child2', 'Kind (2)')
    .set('guest.ringbearer', 'Ringbear(er)')
    .set('contact.title', 'Contact Informatie')
    .set('contact.email', 'Email')
    .set('contact.address', 'Adres')
    .set('contact.zip-code', 'Postcode')
    .set('contact.city', 'Stad')
    .set('contact.country', 'Land')
    .set('contact.phone-number', 'GSM')
    .set('contact.method.preferred', 'Gewenste Contact Methode')
    .set('contact.method.NONE', 'Geen')
    .set('contact.method.EMAIL', 'Email')
    .set('contact.method.PIGEON', 'Postduif')
    .set('contact.method.PHONE', 'GSM')
    .set('main.title', 'Togetherness Foreverness')
    .set('main.subtitle', 'De registratie module voor vrienden en familie voor de trouw van Kim & Bart met een lichte knipoog naar een welbepaald crowdfunding platform.')
    .set('activity.OFFICIAL', 'Het officiële gedeelte')
    .set('activity.PHOTO_SHOOT', 'Photo Shoot')
    .set('activity.CEREMONY', 'Ceremonie')
    .set('activity.RECEPTION', 'Receptie (stretch goal)')
    .set('activity.DINNER', 'Diner')
    .set('activity.PARTY', 'Dessertenbuffet en Avondfeest');

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
