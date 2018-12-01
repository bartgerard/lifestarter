export class Guest {

  constructor(
    public firstName?: string,
    public lastName?: string,
    public diet?: string,
    public allergies?: string[],
    public comment?: string
  ) {
  }

}
