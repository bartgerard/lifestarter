import { describe, expect, it } from 'vitest';

import en from '../../public/i18n/en.json';
import fr from '../../public/i18n/fr.json';
import nl from '../../public/i18n/nl.json';

import { SUPPORTED_LOCALES } from './core/i18n/locale';

interface Dictionary {
  [key: string]: string | Dictionary;
}

/** ngx-translate addresses nested dictionaries by dot path; compare them the same way. */
function flatten(dictionary: Dictionary, prefix = ''): ReadonlyMap<string, string> {
  const entries = new Map<string, string>();

  for (const [key, value] of Object.entries(dictionary)) {
    const path = prefix === '' ? key : `${prefix}.${key}`;

    if (typeof value === 'string') {
      entries.set(path, value);
    } else {
      flatten(value, path).forEach((nested, nestedPath) => entries.set(nestedPath, nested));
    }
  }

  return entries;
}

const dutch = flatten(nl);

const dictionaries = new Map<string, ReadonlyMap<string, string>>([
  ['nl', dutch],
  ['en', flatten(en)],
  ['fr', flatten(fr)],
]);

/** `{{name}}` and friends: ngx-translate substitutes these, so they must match across languages. */
function placeholders(value: string): string {
  return [...value.matchAll(/\{\{\s*(\w+)\s*\}\}/g)]
    .map((match) => match[1])
    .sort()
    .join();
}

function entriesOf(locale: string): ReadonlyMap<string, string> {
  return dictionaries.get(locale) ?? new Map();
}

describe('translations', () => {
  it('ships a dictionary for every published language', () => {
    expect([...dictionaries.keys()].sort()).toEqual([...SUPPORTED_LOCALES].sort());
  });

  it('is not empty, so a broken import cannot make the other checks pass', () => {
    expect(dutch.size).toBeGreaterThan(100);
  });

  it.each(['en', 'fr'])('has exactly the keys of the Dutch source in %s', (locale) => {
    expect([...entriesOf(locale).keys()].sort()).toEqual([...dutch.keys()].sort());
  });

  it.each([...SUPPORTED_LOCALES])('leaves no value blank in %s', (locale) => {
    const blank = [...entriesOf(locale)]
      .filter(([, value]) => value.trim() === '')
      .map(([key]) => key);

    expect(blank).toEqual([]);
  });

  it.each(['en', 'fr'])('interpolates the same parameters as the Dutch source in %s', (locale) => {
    const translated = entriesOf(locale);

    const mismatched = [...dutch]
      .filter(([key, value]) => placeholders(value) !== placeholders(translated.get(key) ?? ''))
      .map(([key]) => key);

    expect(mismatched).toEqual([]);
  });
});
