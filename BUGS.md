# Bugs found while rewriting the client on Angular 22

Every bug below was found by running the built application in a real (headless) browser after the
rewrite compiled cleanly, and **all of them are fixed**. None was caught by `ng build`, `ng lint`
or the unit tests — which is the point of writing them down.

The pattern is worth noting: most of them are cases where an API *looked* like it behaved one way
and quietly behaved another — an invalid `var()` that is dropped instead of reported, a
`hasValue()` that is always true, a `value()` that throws. A green build says nothing about that.

---

## 1. A failing `httpResource` took the page down with it

**Symptom** — every page load logged `ERROR Http failure response for /api/waves/current: 404`,
several times over.

**Cause** — `GET /api/waves/current` answers `404` once the last RSVP wave has closed. That is
part of the contract, not an error. The resource was declared with a `defaultValue`, so it looked
safe:

```ts
readonly currentWave = httpResource<Wave | null>(() => `${this.baseUrl}/waves/current`, {
  defaultValue: null,
});
```

It is not. Reading `resource.value()` **rethrows the loader's error**, and `defaultValue` only
applies while the resource is loading or idle — confirmed in
`@angular/core/fesm2022/_resource-chunk.mjs`:

```js
if (!isResolved(streamValue)) {
  throw new ResourceValueError(this.error());
}
```

So the throw happened inside the `daysLeft` computed, i.e. during template evaluation. With a
handful of read-only endpoints all fetched at root scope, *any* one of them being unreachable
would have blanked whatever page read it.

**Fix** — `valueOr()` in `core/api.ts`, which every reference endpoint now goes through:

```ts
export function valueOr<T>(resource: HttpResourceRef<T>, fallback: NoInfer<T>): Signal<T> {
  return computed(() => (resource.hasValue() ? resource.value() : fallback));
}
```

`hasValue()` is false in the error state, so the fallback wins and nothing throws.

---

## 2. Submitting the RSVP silently did nothing

**Symptom** — the wizard reached the review step, the submit button did its thing, and the form
came back with the generic failure notice. No registration ever reached the backend.

**Cause** — two bugs from one root. The pledge is bound from a query parameter:

```ts
readonly pledge = input('');   // wrong
```

`withComponentInputBinding()` sets a missing query parameter to `undefined`, which **overrides the
input's default**. So `pledgeName` became `undefined` rather than `''`, and:

- `activitiesUrl()` guarded on `pledgeName !== ''`, which `undefined` passes, so the activity
  lookup went out as `…&pledge=undefined`;
- `toRegistration()` called `orNull(draft.pledgeName)` → `undefined.trim()` → `TypeError`, thrown
  inside the submit action's `try`, caught, and reported as a plain submission failure.

The `catch` that was meant to handle a network error was swallowing a programming error.

**Fix** — normalise at the boundary, where the `undefined` enters:

```ts
readonly pledge = input('', { transform: (value: string | undefined) => value ?? '' });
```

---

## 3. The language switcher showed the wrong language

**Symptom** — with the browser asking for `en-US`, the page rendered in English and
`<html lang>` was `en`, but the switcher `<select>` displayed *Nederlands*.

**Cause** —

```html
<select [value]="locale()">
  @for (option of locales; track option) { <option [value]="option">…</option> }
</select>
```

The `value` property is set on the `<select>` before its `@for` has created any `<option>`
children. Assigning `value` to a select with no matching option is a no-op, and nothing re-applies
it afterwards, so the control fell back to displaying its first option.

**Fix** — mark the selection on the option, which is evaluated once the option exists:

```html
<option [value]="option" [selected]="option === locale()">…</option>
```

---

## 4. A detected language was remembered as if it had been chosen

**Symptom** — no visible symptom; found while writing the test for #3.

**Cause** — `LanguageService` resolved the initial locale from `navigator.languages` and then ran
it through the same `use()` method the switcher calls, which persists to `localStorage`. A guess
therefore became indistinguishable from a decision: a visitor whose browser happened to ask for
English on their first visit would be pinned to English forever, even after fixing their browser
settings, and the documented rule "an explicit earlier choice wins over the browser's preferences"
could never apply.

**Fix** — split the two. `apply()` switches language; `use()` switches *and* remembers, and only
the switcher calls it.

---

## 5. `.toSorted()` did not compile

**Symptom** — the first `ng build` failed with four errors on `Array.prototype.toSorted`.

**Cause** — the CLI's generated `tsconfig.json` targets `ES2022`. `toSorted()` (used to sort
pledges without mutating the array behind a signal) is ES2023.

**Fix** — `target: "ES2023"` with an explicit `lib`. Worth keeping in mind for any other
immutable-array method: `toSpliced`, `toReversed`, `with`.

---

## 6. The client image was pinned to a Node that cannot build the app

**Symptom** — would have failed the first `docker compose build web`.

**Cause** — `client/Dockerfile` still said `FROM node:14-alpine`, correct for Angular 10 and
impossible for Angular 22 (`^22.22.3 || ^24.15.0 || >=26.0.0`).

**Fix** — `node:24-alpine`. Note the host machine has Node 25, which Angular 22 also rejects, so
**every** `npm`/`ng` command for this project has to run in a container:

```bash
docker run --rm -v "$PWD/client":/app -w /app node:24-alpine npx ng build
```

---

## 7. An undefined CSS token silently flattened four pages

**Symptom** — the campaign text ran straight into the pledge cards, and the registration, updates
and special pages began hard against the tab bar with no breathing room at all. This is most of
what made the rewrite look like an extreme departure from the original rather than a restyling.

**Cause** — `--space-7` was used in four stylesheets and declared in none:

| File | Declaration | What actually happened |
| --- | --- | --- |
| `campaign/campaign-page.css` | `gap: var(--space-7)` | `gap` fell back to `normal` |
| `updates/updates-page.css` | `gap`, `padding-block` | no gap, no vertical padding |
| `special/special-page.css` | `padding-block` | no padding |
| `registration/registration-page.css` | `padding-block` | no padding |

An invalid `var()` is not a parse error. The declaration is dropped at computed-value time and the
property falls back to its initial value, so the build, the linter and the browser console all
stayed silent — the only evidence was the rendering.

A second, related gap: `.campaign` set `padding-block-end` but never `padding-block-start`, so
even once the token existed the prose still started flush against the tab bar.

**Fix** — declare `--space-7`, give `.campaign` a top padding, and add `design-tokens.spec.ts`,
which asserts that every `var(--token)` referenced anywhere in the app is declared in `styles.css`
and that tokens are declared nowhere else. The browser pass separately measures the top padding of
every page, so a page can never silently go flush again.

---

## 8. Choosing a pledge deleted the activities you had just ticked

**Symptom** — on the activities step, ticking *Dinner* and *Reception* and then doing almost
anything else — changing the pledge, or stepping back to fix a guest's name and forward again —
left only the default two activities selected. No error, no message.

**Cause** — two mistakes compounding.

An effect reconciled the selection against the available list, which is right in principle: an
activity the party has no access to must not stay selected. But it read that list from a resource
that had not settled.

`httpResource` was created with `defaultValue: []`. That makes `value()` permanently defined,
which makes `hasValue()` permanently **true**, which made the `valueOr()` fallback dead code — it
could no longer tell a real answer from an idle or in-flight one. And when a resource's *request*
changes, Angular drops the retained stream, so `value()` returns the default `[]` until the new
response lands.

The activities URL carries the pledge and the party, so both are trivially easy to change. Every
change produced a window where the available list read as empty, and the effect faithfully pruned
the selection down to nothing.

Worse, the guard that looked like it fixed this — `if (!resource.hasValue()) return;` — did
nothing at all, for exactly the same reason.

**Fix** — drop `defaultValue` wherever `valueOr()` is used, so `hasValue()` means what it says,
and gate the reconciliation on it. `valueOr()` now documents the interaction, since the trap is
invisible at the call site. `registration-activities.spec.ts` selects two activities, changes the
pledge, and asserts the selection survives both the in-flight window and the new response; it
fails against the old code.

A third defect fell out of the same investigation: while the lookup refetched, the template
replaced the whole list with a loading message, so the checkboxes vanished and reappeared under
the user's pointer. The list now stays mounted and reports `aria-busy` instead.

---

## 9. The "Stress" bar was never red — the override targeted a PrimeNG that was not installed

Found while restoring the *Tempus Fugit* progress bars. The original `updates.component.css`
carried, in both `updates` and `special`:

```css
::ng-deep .redBar .ui-progressbar .ui-progressbar-value { background-color: indianred; }
```

`ui-` was PrimeNG's prefix up to version 8. The project ran **PrimeNG 10**, which renames every
class to `p-progressbar` / `p-progressbar-value`. The selector therefore matched nothing, and the
Stress bar rendered in the saga-blue theme colour like all the others. The `redBar` class stayed
in the template for years, describing an intention the stylesheet never carried out.

Nothing reports this. An unmatched selector is not an error — it is the normal case for almost
every rule in a stylesheet — so there is no signal to distinguish "matches nothing because the
element is absent right now" from "matches nothing because the name is three major versions out
of date". `::ng-deep` makes it worse by design: it exists precisely to reach markup the component
does not own and therefore cannot be checked against.

**Fix** — the bars no longer depend on a third-party class contract at all. They are built from a
native `<progress>` inside a wrapper this component owns, so every selector refers to markup in
the same file. The red variant was *not* reinstated: the page is a faithful restoration of what
the original rendered, and what it rendered was blue.

A related detail that was easy to get wrong when reproducing it: PrimeNG centres the percentage
over the **whole** bar, not over the filled part, and shows it whenever the value is non-null —
so the 0 % "Seating plan" bar is an empty trough with `0%` in the middle of it.

---

## How these are guarded now

| | |
| --- | --- |
| #1 | Every reference endpoint goes through `valueOr()`; nothing calls `value()` directly. |
| #2 | `registration-api.spec.ts` asserts `pledge=` is never sent empty; `registration-model.spec.ts` covers the draft → request mapping. |
| #3, #4 | Covered by the browser pass: the switcher's value must equal `<html lang>`, and a detected locale must leave `localStorage` untouched. |
| #5 | Compile-time. |
| #6 | `docker compose build` is part of the verification routine. |
| #7 | `design-tokens.spec.ts` fails on any undeclared token; the browser pass measures every page's top padding. |
| #8 | `registration-activities.spec.ts` covers the refetch window and the settled response, and fails against the old code. |
| #9 | No component styles third-party markup any more, so there is no cross-version class contract left to break. The browser pass measures the bars' computed geometry and colour rather than trusting the stylesheet. |


The browser pass that found #1–#4 drives a headless Chromium against the running Compose stack and
checks console output, failed requests, all three languages, the full four-step RSVP through to a
persisted registration, the live SSE counter, keyboard-only navigation, an accessible-name audit of
every control, and the float-label behaviour. It is not yet committed to the repository — adding
Puppeteer as a dependency is a separate decision — so for now it lives outside the source tree and
is run by hand before a release.
