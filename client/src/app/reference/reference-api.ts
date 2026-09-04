import { Injectable, inject } from '@angular/core';
import { httpResource } from '@angular/common/http';

import { API_BASE_URL, valueOr } from '../core/api';
import { Country, Pledge, RegistrationStatistics, Wave } from './reference-types';

/**
 * The site's read-only data.
 *
 * Every endpoint is exposed as a signal instead of an `ngOnInit` + `subscribe`, backed by an
 * `httpResource`. All of it is fetched once at root scope because the values are small, static
 * and shared across pages.
 */
@Injectable({ providedIn: 'root' })
export class ReferenceApi {
  private readonly baseUrl = inject(API_BASE_URL);

  /*
   * None of these declare a `defaultValue` on purpose — `valueOr` supplies the empty answer, and
   * a `defaultValue` would stop it being able to recognise one.
   */
  private readonly pledgesResource = httpResource<readonly Pledge[]>(
    () => `${this.baseUrl}/pledges`,
  );

  private readonly allergiesResource = httpResource<readonly string[]>(
    () => `${this.baseUrl}/allergies`,
  );

  private readonly dietsResource = httpResource<readonly string[]>(() => `${this.baseUrl}/diets`);

  private readonly countriesResource = httpResource<readonly Country[]>(
    () => `${this.baseUrl}/countries`,
  );

  /** `GET /api/waves/current` answers 404 once every wave has closed. That is not an error. */
  private readonly currentWaveResource = httpResource<Wave>(() => `${this.baseUrl}/waves/current`);

  private readonly statisticsResource = httpResource<RegistrationStatistics>(
    () => `${this.baseUrl}/registrations/statistics`,
  );

  readonly pledges = valueOr(this.pledgesResource, []);
  readonly pledgesLoading = this.pledgesResource.isLoading;

  readonly allergies = valueOr(this.allergiesResource, []);
  readonly diets = valueOr(this.dietsResource, []);
  readonly countries = valueOr(this.countriesResource, []);

  /** `null` while loading and once the last wave has closed. */
  readonly currentWave = valueOr<Wave | null>(this.currentWaveResource, null);

  readonly statistics = valueOr<RegistrationStatistics | null>(this.statisticsResource, null);
}
