import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TranslatePipe } from '@ngx-translate/core';

import { CampaignNav } from '../layout/campaign-nav';

/**
 * How far along each preparation was on 29 April 2019, as published in update #2 — including
 * the thirds that were left unrounded at the time.
 */
const PROGRESS = [
  { key: 'clothing', value: 95 },
  { key: 'favours', value: 70 },
  { key: 'registrations', value: 66.6 },
  { key: 'decoration', value: 33.3 },
  { key: 'ceremony', value: 30 },
  { key: 'speeches', value: 10 },
  { key: 'seating', value: 0 },
  { key: 'stress', value: 99.9 },
] as const;

const MAP_URL =
  'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d964.7636007375653!2d4.626297483820641' +
  '!3d50.87920228515613!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2' +
  '!1s0x47c1602d5a666347%3A0xa5b6888796105264!2sFeestzalen+Bertembos!5e1!3m2!1sen!2sbe' +
  '!4v1556708003173!5m2!1sen!2sbe';

@Component({
  selector: 'app-updates-page',
  imports: [TranslatePipe, CampaignNav],
  templateUrl: './updates-page.html',
  styleUrl: './updates-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdatesPage {
  protected readonly progress = PROGRESS;

  /** A constant, so trusting it does not open the door to injected URLs. */
  protected readonly mapUrl: SafeResourceUrl = inject(DomSanitizer).bypassSecurityTrustResourceUrl(
    MAP_URL,
  );
}
