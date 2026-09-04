/** The languages the site is published in. */
export const SUPPORTED_LOCALES = ['nl', 'en', 'fr'] as const;

export type Locale = (typeof SUPPORTED_LOCALES)[number];

/** Dutch is the couple's own wording, so it doubles as the fallback. */
export const FALLBACK_LOCALE: Locale = 'nl';

/** Endonyms, so every option is readable to the person looking for it. */
export const LOCALE_NAMES: Readonly<Record<Locale, string>> = {
  nl: 'Nederlands',
  en: 'English',
  fr: 'Français',
};

export function isLocale(value: string | null | undefined): value is Locale {
  return value != null && (SUPPORTED_LOCALES as readonly string[]).includes(value);
}

/**
 * Picks the best supported locale for a list of BCP 47 tags, e.g. `navigator.languages`.
 * Region subtags are ignored: `fr-BE` and `fr-CA` both resolve to `fr`.
 */
export function resolveLocale(preferred: readonly string[]): Locale | undefined {
  for (const tag of preferred) {
    const language = tag.split('-')[0]?.toLowerCase();

    if (isLocale(language)) {
      return language;
    }
  }

  return undefined;
}
