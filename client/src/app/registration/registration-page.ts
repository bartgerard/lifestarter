import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Injector,
  afterNextRender,
  computed,
  effect,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { httpResource } from '@angular/common/http';
import { FieldTree, FormField, FormRoot, form } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { API_BASE_URL, valueOr } from '../core/api';
import { ReferenceApi } from '../reference/reference-api';
import { FieldErrors } from './field-errors';
import { FloatLabel } from '../ui/float-label';
import { PhoneMask } from '../ui/phone-mask';
import { FALLBACK_ACTIVITIES, RegistrationApi, activitiesUrl, rolesUrl } from './registration-api';
import {
  CONTACT_METHODS,
  DEFAULT_ROLES,
  GuestDraft,
  RegistrationDraft,
  emptyDraft,
  emptyGuest,
  registrationSchema,
  toRegistration,
} from './registration-model';

/** The wizard steps, in order. */
const STEPS = ['contact', 'guests', 'activities', 'review'] as const;

type Step = (typeof STEPS)[number];

type Status = 'editing' | 'done' | 'failed';

@Component({
  selector: 'app-registration-page',
  imports: [RouterLink, TranslatePipe, FieldErrors, FloatLabel, FormField, FormRoot, PhoneMask],
  templateUrl: './registration-page.html',
  styleUrl: './registration-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistrationPage {
  /**
   * Bound from the `?pledge=` query parameter by `withComponentInputBinding()`, which passes
   * `undefined` when the parameter is absent — hence the transform rather than a plain default.
   */
  readonly pledge = input('', { transform: (value: string | undefined) => value ?? '' });

  private readonly injector = inject(Injector);
  private readonly baseUrl = inject(API_BASE_URL);
  private readonly api = inject(RegistrationApi);
  private readonly reference = inject(ReferenceApi);
  private readonly translate = inject(TranslateService);

  private readonly heading = viewChild<ElementRef<HTMLElement>>('stepHeading');

  protected readonly steps = STEPS;
  protected readonly contactMethods = CONTACT_METHODS;

  protected readonly step = signal<Step>(STEPS[0]);
  protected readonly status = signal<Status>('editing');

  /** Set when a step (or the submission) was blocked by validation, cleared on every move. */
  protected readonly blocked = signal(false);

  private readonly model = signal<RegistrationDraft>(emptyDraft());

  protected readonly form = form(this.model, registrationSchema, {
    injector: this.injector,
    submission: {
      action: async () => {
        try {
          await firstValueFrom(this.api.register(toRegistration(this.model())));
          this.status.set('done');
        } catch {
          this.status.set('failed');
        }
      },
      onInvalid: () => this.blocked.set(true),
    },
  });

  protected readonly diets = this.reference.diets;
  protected readonly allergies = this.reference.allergies;
  protected readonly countries = this.reference.countries;

  protected readonly stepIndex = computed(() => STEPS.indexOf(this.step()));

  protected readonly submitting = computed(() => this.form().submitting());

  /**
   * The primary guest as last confirmed by leaving one of the name fields. Deriving the lookup
   * straight from the model would fire a request on every keystroke.
   */
  private readonly primaryGuest = signal<GuestDraft | undefined>(undefined);

  /** The party as it was when the guests step was completed; drives the activity lookup. */
  private readonly committedParty = signal<readonly GuestDraft[]>([]);

  /*
   * Deliberately no `defaultValue`: it would make `value()` always defined, and `hasValue()`
   * always true, so `valueOr` could no longer tell a real answer from an idle or in-flight one.
   */
  private readonly rolesResource = httpResource<readonly string[]>(
    () => rolesUrl(this.baseUrl, this.primaryGuest()),
    { injector: this.injector },
  );

  private readonly activitiesResource = httpResource<readonly string[]>(
    () => activitiesUrl(this.baseUrl, this.model().pledgeName, this.committedParty()),
    { injector: this.injector },
  );

  /** The API only answers for guests it knows; everyone else gets the standard two roles. */
  private readonly grantedRoles = valueOr(this.rolesResource, DEFAULT_ROLES);

  protected readonly roles = computed<readonly string[]>(() => {
    const granted = this.grantedRoles();

    return granted.length > DEFAULT_ROLES.length ? granted : DEFAULT_ROLES;
  });

  protected readonly availableActivities = valueOr(this.activitiesResource, FALLBACK_ACTIVITIES);
  protected readonly activitiesLoading = this.activitiesResource.isLoading;

  protected readonly guests = computed(() => this.model().guests);
  protected readonly selectedActivities = computed(() => this.model().activities);
  protected readonly selectedPledge = computed(() => this.model().pledgeName);
  protected readonly selectedContactMethod = computed(() => this.model().contact.contactMethod);

  protected readonly canAddGuest = computed(() => this.model().guests.length < this.roles().length);

  /** "Wij … komen naar" for a party, "Ik … kom naar" for one guest, as the original read. */
  protected readonly partySentence = computed(() =>
    this.model().guests.length > 1 ? 'registration.activities.we' : 'registration.activities.i',
  );

  protected readonly partyNames = computed(() =>
    this.model()
      .guests.map((guest) => `${guest.firstName} ${guest.lastName}`.trim())
      .filter((name) => name !== '')
      .join(', '),
  );

  constructor() {
    effect(() => {
      const pledge = this.pledge();

      this.model.update((draft) =>
        draft.pledgeName === pledge ? draft : { ...draft, pledgeName: pledge },
      );
    });

    /*
     * An activity the party turns out not to have access to must not stay selected.
     *
     * This has to wait for a settled answer to the *current* request. A resource whose request
     * has just changed reports no value, and reconciling against that would, on every refetch,
     * delete everything the guest had ticked — and the refetch is trivial to trigger, since the
     * lookup depends on both the pledge and the party.
     */
    effect(() => {
      if (!this.activitiesResource.hasValue()) {
        return;
      }

      const available = this.activitiesResource.value() ?? [];

      this.model.update((draft) => {
        const kept = draft.activities.filter((activity) => available.includes(activity));

        return kept.length === draft.activities.length ? draft : { ...draft, activities: kept };
      });
    });
  }

  protected roleOf(index: number): string {
    return this.model().guests[index]?.role ?? DEFAULT_ROLES[0];
  }

  protected guestName(index: number): string {
    const guest = this.model().guests[index];

    return guest ? `${guest.firstName} ${guest.lastName}`.trim() : '';
  }

  protected commitPrimaryGuest(): void {
    this.primaryGuest.set(this.model().guests[0]);
  }

  protected addGuest(): void {
    this.model.update((draft) => ({
      ...draft,
      guests: [...draft.guests, emptyGuest(this.roles()[draft.guests.length] ?? DEFAULT_ROLES[1])],
    }));
  }

  protected removeGuest(index: number): void {
    this.model.update((draft) => ({
      ...draft,
      guests: draft.guests.filter((_, i) => i !== index),
    }));
  }

  protected hasAllergy(index: number, allergy: string): boolean {
    return this.model().guests[index]?.allergies.includes(allergy) ?? false;
  }

  protected toggleAllergy(index: number, allergy: string, selected: boolean): void {
    this.model.update((draft) => ({
      ...draft,
      guests: draft.guests.map((guest, i) =>
        i !== index
          ? guest
          : {
              ...guest,
              allergies: selected
                ? [...guest.allergies, allergy]
                : guest.allergies.filter((value) => value !== allergy),
            },
      ),
    }));
  }

  protected hasActivity(activity: string): boolean {
    return this.model().activities.includes(activity);
  }

  protected toggleActivity(activity: string, selected: boolean): void {
    this.model.update((draft) => ({
      ...draft,
      activities: selected
        ? [...draft.activities, activity]
        : draft.activities.filter((value) => value !== activity),
    }));
  }

  /** The guest's allergies as one translated, comma-separated line, as the original listed them. */
  protected allergyNames(guest: GuestDraft): string {
    return guest.allergies
      .map((allergy) => this.translate.instant(`allergy.${allergy}`))
      .join(', ');
  }

  protected allergyNamesAt(index: number): string {
    const guest = this.model().guests[index];

    return guest ? this.allergyNames(guest) : '';
  }

  protected back(): void {
    this.goTo(this.stepIndex() - 1);
  }

  protected next(): void {
    const current = this.step();

    if (current === 'contact' && !this.validate(this.form.contact)) {
      return;
    }

    if (current === 'guests') {
      this.commitPrimaryGuest();

      if (!this.validate(this.form.guests)) {
        return;
      }

      this.committedParty.set(this.model().guests);
    }

    this.goTo(this.stepIndex() + 1);
  }

  protected retry(): void {
    this.status.set('editing');
  }

  /** Marks a section and all its descendants as touched, so its errors become visible. */
  private validate<T>(section: FieldTree<T>): boolean {
    const state = section();

    state.markAsTouched();
    this.blocked.set(state.invalid());

    return !state.invalid();
  }

  private goTo(index: number): void {
    this.blocked.set(false);
    this.step.set(STEPS[Math.min(Math.max(index, 0), STEPS.length - 1)]);

    afterNextRender(() => this.heading()?.nativeElement.focus(), { injector: this.injector });
  }
}
