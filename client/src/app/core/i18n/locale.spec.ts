import { describe, expect, it } from 'vitest';

import { FALLBACK_LOCALE, isLocale, resolveLocale } from './locale';

describe('isLocale', () => {
  it('accepts the published languages', () => {
    expect(isLocale('nl')).toBe(true);
    expect(isLocale('en')).toBe(true);
    expect(isLocale('fr')).toBe(true);
  });

  it('rejects anything else, including nothing at all', () => {
    expect(isLocale('de')).toBe(false);
    expect(isLocale('NL')).toBe(false);
    expect(isLocale(null)).toBe(false);
    expect(isLocale(undefined)).toBe(false);
  });
});

describe('resolveLocale', () => {
  it('ignores the region subtag', () => {
    expect(resolveLocale(['fr-BE'])).toBe('fr');
    expect(resolveLocale(['NL-nl'])).toBe('nl');
  });

  it('takes the first supported language, not the first entry', () => {
    expect(resolveLocale(['de-DE', 'es', 'en-GB', 'nl'])).toBe('en');
  });

  it('gives up when nothing matches, so the caller can fall back', () => {
    expect(resolveLocale(['de', 'es'])).toBeUndefined();
    expect(resolveLocale([])).toBeUndefined();
    expect(FALLBACK_LOCALE).toBe('nl');
  });
});
