import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Guest} from "../model/guest";

@Injectable({
  providedIn: 'root'
})
export class GuestService {

  constructor(
    private http: HttpClient
  ) {
  }

  getGuests(): Observable<Guest> {
    return this.http.get<Guest>('http://localhost:8080/guests');
  }

}
