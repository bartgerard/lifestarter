export class ContactInformation {

  constructor(
    public email: string,
    public address: string,
    public zipCode: string,
    public city: string,
    public countryIso3: string,
    public phoneNumber: string
  ) {
  }

}
