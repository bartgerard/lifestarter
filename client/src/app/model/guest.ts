export class Guest {

  constructor(
    public role?: string,
    public firstName?: string,
    public lastName?: string,
    public diet?: string,
    public allergies?: string[],
    public comment?: string
  ) {
  }

}
