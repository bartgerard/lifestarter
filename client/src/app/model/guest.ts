import {FullName} from './full-name';

export class Guest {

  constructor(
    private fullName: FullName,
    private diet: string,
    private comment: string
  ) {
  }

}
