import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { LanguageService } from '../core/i18n/language';
import { LOCALE_NAMES, isLocale } from '../core/i18n/locale';

@Component({
  selector: 'app-language-switcher',
  imports: [TranslatePipe],
  templateUrl: './language-switcher.html',
  styleUrl: './language-switcher.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LanguageSwitcher {
  private readonly language = inject(LanguageService);

  protected readonly locale = this.language.locale;
  protected readonly locales = this.language.locales;
  protected readonly loading = this.language.loading;
  protected readonly names = LOCALE_NAMES;

  protected select(value: string): void {
    if (isLocale(value)) {
      this.language.use(value);
    }
  }
}
