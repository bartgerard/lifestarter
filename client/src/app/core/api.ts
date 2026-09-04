import { InjectionToken, Signal, computed } from '@angular/core';
import { HttpResourceRef } from '@angular/common/http';

/**
 * Base URL of the Lifestarter API.
 *
 * Both `ng serve` (via `proxy.conf.json`) and the nginx container proxy `/api` to the Spring
 * Boot service, so the browser always talks to its own origin and CORS never comes into play.
 */
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => '/api',
});

/**
 * Reads a resource, collapsing to `fallback` whenever it has nothing to say.
 *
 * `value()` rethrows whatever the loader failed with, so one unreachable endpoint would take
 * down every template that reads it. None of this site's data is essential to rendering a page,
 * so failures collapse to the fallback — as does an idle resource, and one whose request has
 * just changed and is therefore loading a different answer than the one it last held.
 *
 * Pass resources **without** a `defaultValue`. A `defaultValue` makes `value()` permanently
 * defined, which makes `hasValue()` permanently true, which makes this fallback dead code — and
 * silently substitutes that default in every state this function exists to handle.
 */
export function valueOr<T>(
  resource: HttpResourceRef<T | undefined>,
  fallback: NoInfer<T>,
): Signal<T> {
  return computed(() => {
    if (!resource.hasValue()) {
      return fallback;
    }

    const value: T | undefined = resource.value();

    return value ?? fallback;
  });
}
