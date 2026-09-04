import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../core/api';
import { GuestDraft, NewRegistration } from './registration-model';

/** The activities everyone may attend when the API has nothing more specific to say. */
export const FALLBACK_ACTIVITIES: readonly string[] = ['CEREMONY', 'PARTY'];

function isNamed(guest: GuestDraft | undefined): guest is GuestDraft {
  return !!guest && guest.firstName.trim() !== '' && guest.lastName.trim() !== '';
}

/**
 * URL for the extra ceremony roles a named guest is entitled to, or `undefined` while the guest
 * has no name yet — which keeps the backing `httpResource` idle.
 */
export function rolesUrl(baseUrl: string, guest: GuestDraft | undefined): string | undefined {
  if (!isNamed(guest)) {
    return undefined;
  }

  const params = new URLSearchParams({
    'first-name': guest.firstName.trim(),
    'last-name': guest.lastName.trim(),
  });

  return `${baseUrl}/access/roles?${params}`;
}

/** URL for the activities a party may attend, falling back to what their pledge includes. */
export function activitiesUrl(
  baseUrl: string,
  pledgeName: string,
  guests: readonly GuestDraft[],
): string | undefined {
  const [first, second] = guests;

  if (!isNamed(first)) {
    return undefined;
  }

  const params = new URLSearchParams({
    'first-first-name': first.firstName.trim(),
    'first-last-name': first.lastName.trim(),
  });

  if (isNamed(second)) {
    params.set('second-first-name', second.firstName.trim());
    params.set('second-last-name', second.lastName.trim());
  }

  if (pledgeName !== '') {
    params.set('pledge', pledgeName);
  }

  return `${baseUrl}/access/activities?${params}`;
}

@Injectable({ providedIn: 'root' })
export class RegistrationApi {
  private readonly baseUrl = inject(API_BASE_URL);
  private readonly http = inject(HttpClient);

  register(registration: NewRegistration): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/registrations`, registration);
  }
}
