import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  map: Map<string, string> = new Map()
    .set("diet.MEAT_EATER", "Vleeseter")
    .set("diet.PESCETARIAN", "Viseter")
    .set("diet.VEGETARIAN", "Vegetariër")
    .set("diet.VEGAN", "Veganist")
    .set("diet.FRUITARIAN", "Fruitariër");

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
