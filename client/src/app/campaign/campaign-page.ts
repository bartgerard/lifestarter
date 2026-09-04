import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { CampaignNav } from '../layout/campaign-nav';
import { ReferenceApi } from '../reference/reference-api';
import { RegistrationEvents } from '../reference/registration-events';
import { PledgeList } from './pledge-list';

const MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

/** The activities shown in the schedule, in the order they happen on the day. */
const SCHEDULE = [
  { time: '10:30', key: 'OFFICIAL' },
  { time: '12:00', key: 'PHOTO_SHOOT' },
  { time: '14:00', key: 'CEREMONY' },
  { time: '15:30', key: 'RECEPTION' },
  { time: '17:00', key: 'DINNER' },
  { time: '20:30', key: 'PARTY' },
] as const;

/** Guest counts at which something extra was promised, Kickstarter style. */
const STRETCH_GOALS = ['2', '42', '140', '150', '160', '10k'] as const;

@Component({
  selector: 'app-campaign-page',
  imports: [RouterLink, TranslatePipe, CampaignNav, PledgeList],
  templateUrl: './campaign-page.html',
  styleUrl: './campaign-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CampaignPage {
  private readonly reference = inject(ReferenceApi);
  private readonly events = inject(RegistrationEvents);

  protected readonly schedule = SCHEDULE;
  protected readonly stretchGoals = STRETCH_GOALS;

  protected readonly wave = this.reference.currentWave;

  /** Server total plus anything the live feed has reported since the page opened. */
  protected readonly guestCount = computed(
    () => (this.reference.statistics()?.totalGuests ?? 0) + this.events.additions(),
  );

  protected readonly live = this.events.live;

  protected readonly daysLeft = computed(() => {
    const deadline = this.wave()?.deadline;

    if (!deadline) {
      return 0;
    }

    const remaining = new Date(deadline).getTime() - Date.now();

    return Math.max(Math.ceil(remaining / MILLISECONDS_PER_DAY), 0);
  });
}
