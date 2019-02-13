import {Guest} from './guest';
import {ContactInformation} from './contact-information';

export class Registration {

  constructor(
    public email: string,
    public guests: Guest[],
    public contactOptions: ContactInformation[],
    public pledgeName: string
  ) {
  }

}
