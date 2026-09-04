import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WritableSignal, provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { beforeEach, describe, expect, it } from 'vitest';

import { RegistrationPage } from './registration-page';
import { DEFAULT_ROLES, GuestDraft, RegistrationDraft, emptyGuest } from './registration-model';

const GRANTED = ['CEREMONY', 'RECEPTION', 'DINNER', 'PARTY'];

/** Reaches past `private`/`protected`; these tests drive the page the way its template does. */
interface Internals {
  model: WritableSignal<RegistrationDraft>;
  committedParty: WritableSignal<readonly GuestDraft[]>;
  availableActivities: () => readonly string[];
  selectedActivities: () => readonly string[];
  toggleActivity(activity: string, selected: boolean): void;
}

describe('RegistrationPage activities', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTranslateService({ fallbackLang: 'en' }),
      ],
    });

    http = TestBed.inject(HttpTestingController);
  });

  function open() {
    const fixture = TestBed.createComponent(RegistrationPage);
    const page = fixture.componentInstance as unknown as Internals;

    // A party has to be committed before the activity lookup has anything to ask about.
    const guests: readonly GuestDraft[] = [
      { ...emptyGuest(DEFAULT_ROLES[0]), firstName: 'Ada', lastName: 'Lovelace' },
    ];

    page.model.update((draft) => ({ ...draft, guests }));
    page.committedParty.set(guests);

    return { fixture, page };
  }

  /**
   * Answers every request the page has open so the fixture can settle. Activity lookups get
   * `granted`; the reference data behind the other steps gets an empty list, which is all these
   * tests need.
   */
  async function settle(fixture: ComponentFixture<unknown>, granted?: readonly string[]) {
    for (let attempt = 0; attempt < 8; attempt += 1) {
      fixture.detectChanges();
      await Promise.resolve();

      const open = http.match(() => true);

      if (open.length === 0) {
        break;
      }

      for (const request of open) {
        request.flush(request.request.url.includes('/access/activities') ? (granted ?? []) : []);
      }
    }

    fixture.detectChanges();
    await Promise.resolve();
  }

  it('keeps the guest\u2019s choices while the activity list is refetched', async () => {
    const { fixture, page } = open();

    await settle(fixture, GRANTED);
    expect(page.availableActivities()).toEqual(GRANTED);

    page.toggleActivity('DINNER', true);
    page.toggleActivity('RECEPTION', true);
    await settle(fixture, GRANTED);

    expect(page.selectedActivities()).toEqual(['DINNER', 'RECEPTION']);

    /*
     * The regression. The lookup URL carries the pledge and the party, so picking a reward — or
     * stepping back to edit a guest and forward again — starts a new request. A resource whose
     * request has changed reports no value, and the reconciling effect used to run against that
     * empty answer and delete everything the guest had ticked.
     */
    fixture.componentRef.setInput('pledge', 'DINNER GUEST');
    fixture.detectChanges();
    await Promise.resolve();

    expect(page.selectedActivities()).toEqual(['DINNER', 'RECEPTION']);

    await settle(fixture, GRANTED);

    expect(page.selectedActivities()).toEqual(['DINNER', 'RECEPTION']);
  });

  it('drops an activity the party turns out not to have access to', async () => {
    const { fixture, page } = open();

    await settle(fixture, GRANTED);

    page.toggleActivity('DINNER', true);
    page.toggleActivity('PARTY', true);
    await settle(fixture, GRANTED);

    expect(page.selectedActivities()).toEqual(['DINNER', 'PARTY']);

    fixture.componentRef.setInput('pledge', 'LATE GUEST');
    await settle(fixture, ['CEREMONY', 'PARTY']);

    // Reconciliation still happens — it just waits for an answer before acting on it.
    expect(page.selectedActivities()).toEqual(['PARTY']);
  });
});
