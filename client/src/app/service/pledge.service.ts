import {Injectable} from '@angular/core';
import {Pledge} from '../model/pledge';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PledgeService {

  pledgeList: Pledge[] = [
    new Pledge(
      'FRIENDS',
      0,
      'Toegang tot (bijna) alle festiviteiten. Mogelijk vallen om budgetaire redenen bepaalde onderdelen weg, maar voel jullie daardoor zeker niet minder welkom!',
      [
        '2x Almost full access to the festivities'
      ]
    ),
    new Pledge(
      'FAMILY',
      0,
      '',
      [
        'Nx Full access to the festivities (with N > 0)'
      ]
    ),
    new Pledge(
      'VIP',
      500,
      'This reward includes a print of the wedding book.',
      [
        '2x Full access to the festivities',
        '1x Wedding Book (€250)'
      ]
    ),
    new Pledge(
      'VIP+',
      800,
      'Diamonds are forever en zo ook het trouwboek in deze pledge. De fotograaf drukt het finale fotoboek op ultra duurzaam materiaal. Zo kun je onze mooie dag, nog door enkele generaties na ons laten verder beleven.',
      [
        '2x Full access to the festivities',
        '1x Diamond Wedding Book (€600)'
      ]
    ),
    new Pledge(
      'YOLO',
      900,
      'Hou jij van uitdagingen? Wij ook! Voor deze pledge zal de bruidegom één politiek correct woord naar keuze verwerken in één van zijn speeches. Niet voor merk getinte woorden. Zie hiervoor de commerciële pledge.',
      [
        '2x Full access to the festivities',
        '1x Politiek correct woord naar keuze in speech bruidegom'
      ]
    ),
    new Pledge(
      'COMMERCIAL',
      1500,
      'Misschien heb jij een product waar je graag reclame voor wilt maken. Denk bijvoorbeeld aan het heerlijke bier Valduc. Met deze pledge krijg je een vermelding op deze site en tijdens één van de speeches.',
      [
        '2x Full access to the festivities',
        '1x Vermelding van jouw product'
      ]
    )
  ];

  constructor() {
  }

  pledges(): Observable<Pledge[]> {
    return of(this.pledgeList);
  }

}
