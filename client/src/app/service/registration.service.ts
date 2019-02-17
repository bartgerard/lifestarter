import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Registration} from '../model/registration';
import {Observable} from 'rxjs';
import {Guest} from '../model/guest';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  public JSON_HEADERS: HttpHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(
    private http: HttpClient
  ) {
  }

  register(
    registration: Registration
  ): Observable<Registration> {
    return this.http
      .put<Registration>(environment.serverUrl + '/registrations', registration, {headers: this.JSON_HEADERS});
  }

  nbGuests(): Observable<number> {
    return this.http.get<number>(environment.serverUrl + '/registrations/nb-guests');
  }

  vipCheck(
    guest: Guest
  ): Observable<string[]> {
    return this.http.get<string[]>(
      environment.serverUrl + '/registrations/vip-check',
      {
        params: new HttpParams()
          .set('first-name', guest.firstName)
          .set('last-name', guest.lastName)
      }
    );
  }

}
