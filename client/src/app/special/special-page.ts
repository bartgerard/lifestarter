import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { CampaignNav } from '../layout/campaign-nav';

/**
 * A one-off page from August 2020 asking a friend to become a godfather.
 *
 * It is real content rather than a demo, so it is kept — including the long dramatic pauses,
 * which are now CSS spacing instead of inline styles.
 */
@Component({
  selector: 'app-special-page',
  imports: [TranslatePipe, CampaignNav],
  templateUrl: './special-page.html',
  styleUrl: './special-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpecialPage {
  protected readonly accepted = signal(false);
  protected readonly confirmed = signal(false);

  protected toggleAccepted(accepted: boolean): void {
    this.accepted.set(accepted);

    if (!accepted) {
      this.confirmed.set(false);
    }
  }

  protected confirm(): void {
    this.confirmed.set(true);
  }
}
