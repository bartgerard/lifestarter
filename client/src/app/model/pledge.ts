export class Pledge {

  constructor(
    private name: string,
    private price: number,
    private description: string,
    private contents: string[],
    private amount: number = 0
  ) {
  }

}
