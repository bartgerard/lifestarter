export class Pledge {

  constructor(
    public name: string,
    public price: number,
    public description: string,
    public contents: string[],
    public available: boolean,
    public amount: number = 0
  ) {
  }

}
