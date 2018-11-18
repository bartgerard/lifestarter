import {Guest} from './guest';
import {ContactInformation} from './contact-information';

export class Registration {

  constructor(
    public email?: string,
    public guests?: Guest[],
    public contactInformation?: ContactInformation,
    public pledgeName?: string
  ) {
  }

}
