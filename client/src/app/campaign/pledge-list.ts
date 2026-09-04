import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { UpperCasePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { LanguageService } from '../core/i18n/language';
import { ReferenceApi } from '../reference/reference-api';
import { Pledge } from '../reference/reference-types';

interface PledgeView extends Pledge {
  readonly formattedPrice: string;
}

@Component({
  selector: 'app-pledge-list',
  imports: [RouterLink, TranslatePipe, UpperCasePipe],
  templateUrl: './pledge-list.html',
  styleUrl: './pledge-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PledgeList {
  private readonly reference = inject(ReferenceApi);
  private readonly language = inject(LanguageService);

  protected readonly loading = this.reference.pledgesLoading;

  /**
   * `Intl` is used rather than `CurrencyPipe` because the pipe needs Angular locale data to be
   * registered per language, which would mean shipping locale bundles just to render "€25".
   */
  protected readonly pledges = computed<readonly PledgeView[]>(() => {
    const format = new Intl.NumberFormat(this.language.locale(), {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0,
    });

    return this.reference
      .pledges()
      .toSorted((a, b) => a.orderId - b.orderId)
      .map((pledge) => ({ ...pledge, formattedPrice: format.format(pledge.price) }));
  });
}
