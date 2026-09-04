import { DOCUMENT, Injectable, effect, inject, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import {
  FALLBACK_LOCALE,
  Locale,
  SUPPORTED_LOCALES,
  isLocale,
  resolveLocale,
} from './locale';

const STORAGE_KEY = 'lifestarter.locale';

/**
 * Owns the active language.
 *
 * `TranslateService` already exposes `currentLang` as a signal, but it is `null` until the
 * first dictionary resolves and it knows nothing about persistence or `<html lang>`. This
 * service is the single place that decides which language wins and keeps the document in
 * sync with it.
 */
@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translate = inject(TranslateService);
  private readonly document = inject(DOCUMENT);

  private readonly active = signal<Locale>(FALLBACK_LOCALE);

  /** The language currently rendered. Never null, unlike `TranslateService.currentLang`. */
  readonly locale = this.active.asReadonly();

  /** True while a dictionary is being fetched, for spinners and `aria-busy`. */
  readonly loading = this.translate.isLoading;

  readonly locales = SUPPORTED_LOCALES;

  constructor() {
    this.translate.addLangs([...SUPPORTED_LOCALES]);
    this.translate.setFallbackLang(FALLBACK_LOCALE);

    // Deliberately not remembered: a language merely *detected* from the browser must stay
    // distinguishable from one the visitor picked, or a stale detection would outlive it.
    this.apply(this.preferredLocale());

    effect(() => {
      this.document.documentElement.lang = this.active();
    });
  }

  /** Switches language on the visitor's request and remembers the choice. */
  use(locale: Locale): void {
    this.apply(locale);
    this.remember(locale);
  }

  private apply(locale: Locale): void {
    if (locale === this.active() && this.translate.currentLang() === locale) {
      return;
    }

    this.active.set(locale);
    this.translate.use(locale);
  }

  /** An explicit earlier choice wins over the browser's preferences. */
  private preferredLocale(): Locale {
    const stored = this.read(STORAGE_KEY);

    if (isLocale(stored)) {
      return stored;
    }

    return resolveLocale(this.document.defaultView?.navigator.languages ?? []) ?? FALLBACK_LOCALE;
  }

  private read(key: string): string | null {
    // Storage throws when cookies are blocked, and a language preference is not worth a crash.
    try {
      return this.document.defaultView?.localStorage.getItem(key) ?? null;
    } catch {
      return null;
    }
  }

  private remember(locale: Locale): void {
    try {
      this.document.defaultView?.localStorage.setItem(STORAGE_KEY, locale);
    } catch {
      // Ignored on purpose: the choice simply will not survive a reload.
    }
  }
}
