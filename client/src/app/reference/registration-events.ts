import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';

import { API_BASE_URL } from '../core/api';

/**
 * Subscribes to the server-sent RSVP feed and counts what arrives.
 *
 * The counter is deliberately relative: the page renders `statistics + liveAdditions`, so a
 * visitor watching the page sees the guest total tick up as other people reply, without
 * re-fetching the statistics endpoint.
 */
@Injectable({ providedIn: 'root' })
export class RegistrationEvents {
  private readonly baseUrl = inject(API_BASE_URL);

  private readonly received = signal(0);
  private source?: EventSource;

  /** How many RSVPs arrived since this page was opened. */
  readonly additions = this.received.asReadonly();

  readonly live = computed(() => this.received() > 0);

  constructor() {
    inject(DestroyRef).onDestroy(() => this.close());
    this.connect();
  }

  private connect(): void {
    if (typeof EventSource === 'undefined') {
      return;
    }

    const source = new EventSource(`${this.baseUrl}/events/stream`);

    source.addEventListener('registration-added', () => this.received.update((n) => n + 1));
    // The browser reconnects on its own; the stream simply going quiet is not worth reporting.
    source.addEventListener('error', () => undefined);

    this.source = source;
  }

  private close(): void {
    this.source?.close();
    this.source = undefined;
  }
}
