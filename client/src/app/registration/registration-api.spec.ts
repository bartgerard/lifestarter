import { describe, expect, it } from 'vitest';

import { activitiesUrl, rolesUrl } from './registration-api';
import { GuestDraft, emptyGuest } from './registration-model';

function guest(firstName: string, lastName: string): GuestDraft {
  return { ...emptyGuest('guest.main'), firstName, lastName };
}

const ada = guest('Ada', 'Lovelace');
const alan = guest(' Alan ', ' Turing ');

describe('rolesUrl', () => {
  it('is undefined while the guest has no name, which keeps the resource idle', () => {
    expect(rolesUrl('/api', undefined)).toBeUndefined();
    expect(rolesUrl('/api', guest('', ''))).toBeUndefined();
    expect(rolesUrl('/api', guest('Ada', '   '))).toBeUndefined();
  });

  it('trims and encodes the name', () => {
    expect(rolesUrl('/api', guest(' Ada ', "O'Neil"))).toBe(
      '/api/access/roles?first-name=Ada&last-name=O%27Neil',
    );
  });
});

describe('activitiesUrl', () => {
  it('is undefined until the first guest is named', () => {
    expect(activitiesUrl('/api', '', [])).toBeUndefined();
    expect(activitiesUrl('/api', 'family', [guest('Ada', '')])).toBeUndefined();
  });

  it('omits the second guest while that row is still blank', () => {
    expect(activitiesUrl('/api', '', [ada, emptyGuest('guest.plus1')])).toBe(
      '/api/access/activities?first-first-name=Ada&first-last-name=Lovelace',
    );
  });

  it('includes the whole party and the pledge', () => {
    expect(activitiesUrl('/api', 'early bird friends', [ada, alan])).toBe(
      '/api/access/activities?first-first-name=Ada&first-last-name=Lovelace' +
        '&second-first-name=Alan&second-last-name=Turing&pledge=early+bird+friends',
    );
  });

  it('never sends an empty pledge, which the API would read as a tier name', () => {
    expect(activitiesUrl('/api', '', [ada])).not.toContain('pledge=');
  });
});
