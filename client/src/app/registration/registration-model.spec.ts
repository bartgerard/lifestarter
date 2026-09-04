import { describe, expect, it } from 'vitest';

import { RegistrationDraft, emptyDraft, emptyGuest, toRegistration } from './registration-model';

function draft(overrides: Partial<RegistrationDraft> = {}): RegistrationDraft {
  return { ...emptyDraft(), ...overrides };
}

describe('toRegistration', () => {
  it('sends empty optional fields as null, not as ""', () => {
    const request = toRegistration(draft({ contact: { ...emptyDraft().contact, email: 'a@b.c' } }));
    const [contact] = request.contactOptions;

    expect(contact.address).toBeNull();
    expect(contact.zipCode).toBeNull();
    expect(contact.city).toBeNull();
    expect(contact.countryIso3).toBeNull();
    expect(contact.phoneNumber).toBeNull();
    expect(request.pledgeName).toBeNull();
  });

  it('sends an unset diet as null, because "" is not a valid enum value', () => {
    const [guest] = toRegistration(draft()).guests;

    expect(guest.diet).toBeNull();
  });

  it('trims everything a guest typed', () => {
    const request = toRegistration(
      draft({
        contact: { ...emptyDraft().contact, email: '  ada@example.org  ' },
        guests: [{ ...emptyGuest('WITNESS'), firstName: ' Ada ', lastName: ' Lovelace ' }],
      }),
    );

    expect(request.email).toBe('ada@example.org');
    expect(request.guests[0]).toMatchObject({ firstName: 'Ada', lastName: 'Lovelace' });
  });

  it('mirrors the contact e-mail onto the contact option the API expects', () => {
    const request = toRegistration(
      draft({ contact: { ...emptyDraft().contact, email: 'ada@example.org' } }),
    );

    expect(request.contactOptions).toHaveLength(1);
    expect(request.contactOptions[0].email).toBe('ada@example.org');
    expect(request.contactOptions[0].contactMethod).toBe('EMAIL');
  });

  it('copies the selections rather than sharing the draft arrays', () => {
    const source = draft({ activities: ['CEREMONY'] });
    const request = toRegistration(source);

    expect(request.activities).toEqual(['CEREMONY']);
    expect(request.activities).not.toBe(source.activities);
  });
});
