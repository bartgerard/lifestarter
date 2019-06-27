import {Guest} from '../model/guest';

export class GuestAdded {

  constructor(
    public role: string,
    public guest: Guest
  ) {
  }

}
