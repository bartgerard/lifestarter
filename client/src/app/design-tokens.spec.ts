import { readFileSync, readdirSync } from 'node:fs';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';

const SRC = join(import.meta.dirname, '..');
const GLOBAL_STYLESHEET = join(SRC, 'styles.css');

function stylesheets(directory: string): string[] {
  return readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const path = join(directory, entry.name);

    if (entry.isDirectory()) {
      return stylesheets(path);
    }

    return entry.name.endsWith('.css') ? [path] : [];
  });
}

function matches(source: string, pattern: RegExp): string[] {
  return [...source.matchAll(pattern)].map(([, captured]) => captured);
}

const globalStyles = readFileSync(GLOBAL_STYLESHEET, 'utf8');

/** Only declarations, so `var(--x)` on the right-hand side is never mistaken for one. */
const declared = new Set(matches(globalStyles, /^\s*(--[\w-]+)\s*:/gm));

describe('design tokens', () => {
  it('declares every token the application refers to', () => {
    const undeclared = stylesheets(SRC).flatMap((path) => {
      const source = readFileSync(path, 'utf8');

      return (
        matches(source, /var\(\s*(--[\w-]+)\s*\)/g)
          // A token with a fallback — `var(--flow, 1rem)` — is allowed to be absent by design.
          .filter((token) => !declared.has(token))
          .map((token) => `${path.slice(SRC.length + 1)}: ${token}`)
      );
    });

    /*
     * An invalid `var()` is not a parse error. The declaration is dropped at computed-value
     * time and the property silently falls back to its initial value, so a typo'd token
     * reaches production without the build, the linter or the browser console saying a word.
     * `--space-7` did exactly that: four pages lost their gaps and padding.
     */
    expect(undeclared).toEqual([]);
  });

  it('declares tokens only in the global stylesheet, so the scale stays in one place', () => {
    const strays = stylesheets(SRC)
      .filter((path) => path !== GLOBAL_STYLESHEET)
      .flatMap((path) => {
        const declarations = matches(readFileSync(path, 'utf8'), /^\s*(--[\w-]+)\s*:/gm);

        return declarations.map((token) => `${path.slice(SRC.length + 1)}: ${token}`);
      });

    expect(strays).toEqual([]);
  });
});
