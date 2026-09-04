import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

/** The Concept / Updates / Comments tab strip that sits under the campaign hero. */
@Component({
  selector: 'app-campaign-nav',
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './campaign-nav.html',
  styleUrl: './campaign-nav.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CampaignNav {}
