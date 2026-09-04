import {
  applyEach,
  applyWhen,
  email,
  maxLength,
  required,
  schema,
  validate,
} from '@angular/forms/signals';

import { isCompletePhoneNumber } from '../ui/phone-mask';

/** The contact methods the API accepts, in the order they are offered. */
export const CONTACT_METHODS = ['EMAIL', 'PHONE', 'PIGEON', 'NONE'] as const;

export type ContactMethod = (typeof CONTACT_METHODS)[number];

/**
 * The RSVP as it is being edited.
 *
 * Every field is a plain, always-present primitive: signal forms edit a single `WritableSignal`
 * holding this object, and optional/undefined members would only make the templates and the
 * schema harder to read. Emptiness is expressed as `''` and mapped away when the request is built.
 */
export interface GuestDraft {
  readonly role: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly diet: string;
  readonly allergies: readonly string[];
  readonly comment: string;
}

export interface ContactDraft {
  readonly email: string;
  readonly address: string;
  readonly zipCode: string;
  readonly city: string;
  readonly countryIso3: string;
  readonly phoneNumber: string;
  readonly contactMethod: ContactMethod;
}

export interface RegistrationDraft {
  readonly contact: ContactDraft;
  readonly guests: readonly GuestDraft[];
  readonly activities: readonly string[];
  readonly pledgeName: string;
}

/** The roles offered before the API tells us a guest is entitled to more. */
export const DEFAULT_ROLES: readonly string[] = ['guest.main', 'guest.plus1'];

export function emptyGuest(role: string): GuestDraft {
  return { role, firstName: '', lastName: '', diet: '', allergies: [], comment: '' };
}

export function emptyDraft(pledgeName = ''): RegistrationDraft {
  return {
    contact: {
      email: '',
      address: '',
      zipCode: '',
      city: '',
      countryIso3: '',
      phoneNumber: '',
      contactMethod: 'EMAIL',
    },
    guests: [emptyGuest(DEFAULT_ROLES[0])],
    activities: [],
    pledgeName,
  };
}

/**
 * Client-side mirror of the Bean Validation constraints on `NewRegistrationTo`, so a guest is told
 * what is wrong before the request leaves the browser rather than after a 400.
 */
export const registrationSchema = schema<RegistrationDraft>((draft) => {
  required(draft.contact.email);
  email(draft.contact.email);
  maxLength(draft.contact.email, 320);
  maxLength(draft.contact.address, 200);
  maxLength(draft.contact.zipCode, 20);
  maxLength(draft.contact.city, 100);
  maxLength(draft.contact.phoneNumber, 50);

  /*
   * The mask lets a number be abandoned half-typed — `autoClear="false"` in the original — so
   * an incomplete one has to be reported rather than sent on. An empty field is still fine;
   * whether a number is required at all is decided by the contact method below.
   */
  validate(draft.contact.phoneNumber, ({ value }) => {
    const entered = value();

    return entered === '' || isCompletePhoneNumber(entered)
      ? null
      : { kind: 'phoneNumber' as const };
  });

  applyWhen(
    draft.contact,
    ({ value }) => value().contactMethod === 'PHONE',
    (contact) => required(contact.phoneNumber),
  );

  applyWhen(
    draft.contact,
    ({ value }) => value().contactMethod === 'PIGEON',
    (contact) => {
      required(contact.address);
      required(contact.zipCode);
      required(contact.city);
      required(contact.countryIso3);
    },
  );

  // `applyEach` applies the per-guest rules to whichever rows exist, so guests added or removed
  // at runtime are validated without re-creating the form.
  applyEach(draft.guests, (guest) => {
    required(guest.firstName);
    maxLength(guest.firstName, 100);
    required(guest.lastName);
    maxLength(guest.lastName, 100);
    maxLength(guest.comment, 1000);
  });
});

/** Request body for `POST /api/registrations`. */
export interface NewRegistration {
  readonly email: string;
  readonly guests: readonly GuestRequest[];
  readonly contactOptions: readonly ContactOptionRequest[];
  readonly pledgeName: string | null;
  readonly activities: readonly string[];
}

export interface GuestRequest {
  readonly role: string | null;
  readonly firstName: string;
  readonly lastName: string;
  readonly diet: string | null;
  readonly allergies: readonly string[];
  readonly comment: string | null;
}

export interface ContactOptionRequest {
  readonly email: string | null;
  readonly address: string | null;
  readonly zipCode: string | null;
  readonly city: string | null;
  readonly countryIso3: string | null;
  readonly phoneNumber: string | null;
  readonly contactMethod: ContactMethod;
}

/** Optional text fields are sent as `null` rather than `""`, which the API maps to "not given". */
function orNull(value: string): string | null {
  const trimmed = value.trim();

  return trimmed === '' ? null : trimmed;
}

/** Turns the draft into the request body the API expects, dropping everything left empty. */
export function toRegistration(draft: RegistrationDraft): NewRegistration {
  const contact = draft.contact;

  return {
    email: contact.email.trim(),
    guests: draft.guests.map((guest) => ({
      role: orNull(guest.role),
      firstName: guest.firstName.trim(),
      lastName: guest.lastName.trim(),
      diet: orNull(guest.diet),
      allergies: [...guest.allergies],
      comment: orNull(guest.comment),
    })),
    contactOptions: [
      {
        email: contact.email.trim(),
        address: orNull(contact.address),
        zipCode: orNull(contact.zipCode),
        city: orNull(contact.city),
        countryIso3: orNull(contact.countryIso3),
        phoneNumber: orNull(contact.phoneNumber),
        contactMethod: contact.contactMethod,
      },
    ],
    pledgeName: orNull(draft.pledgeName),
    activities: [...draft.activities],
  };
}
