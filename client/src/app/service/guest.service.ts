import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Guest} from '../model/guest';
import {WebConfig} from '../model/web-config';

@Injectable({
  providedIn: 'root'
})
export class GuestService {

  constructor(
    private http: HttpClient
  ) {
  }

  getGuests(): Observable<Guest> {
    return this.http.get<Guest>(WebConfig.baseUri + '/guests');
  }

}
