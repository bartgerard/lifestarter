/** Read models mirroring the Lifestarter API's JSON payloads. */

export interface Country {
  readonly iso2: string;
  readonly iso3: string;
  readonly name: string;
}

export interface Pledge {
  readonly name: string;
  readonly orderId: number;
  readonly price: number;
  readonly description: string;
  readonly contents: readonly string[];
  /** `0` means unlimited. */
  readonly limit: number;
  /** `false` when the pledge is closed or already full. */
  readonly available: boolean;
  /** How many guests already picked this pledge. */
  readonly amount: number;
}

export interface Wave {
  readonly label: string;
  readonly deadline: string;
}

export interface RegistrationStatistics {
  readonly totalRegistrations: number;
  readonly totalGuests: number;
  readonly dinnerGuests: number;
  readonly guestsPerPledge: Readonly<Record<string, number>>;
}
