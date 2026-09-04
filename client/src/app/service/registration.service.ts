import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Registration} from '../model/registration';
import {RegistrationStatistics} from '../model/registration-statistics';
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
      .post<Registration>(environment.apiUrl + '/registrations', registration, {headers: this.JSON_HEADERS});
  }

  statistics(): Observable<RegistrationStatistics> {
    return this.http.get<RegistrationStatistics>(environment.apiUrl + '/registrations/statistics');
  }

  roles(
    guest: Guest
  ): Observable<string[]> {
    return this.http.get<string[]>(
      environment.apiUrl + '/access/roles',
      {
        params: new HttpParams()
          .set('first-name', guest.firstName)
          .set('last-name', guest.lastName)
      }
    );
  }

  activities(
    pledge: string,
    guests: Guest[]
  ): Observable<string[]> {
    let params = new HttpParams();

    if (pledge) {
      params = params.set('pledge', pledge);
    }

    if (guests[0]) {
      params = params
        .set('first-first-name', guests[0].firstName)
        .set('first-last-name', guests[0].lastName);
    }

    if (guests[1]) {
      params = params
        .set('second-first-name', guests[1].firstName)
        .set('second-last-name', guests[1].lastName);
    }

    return this.http.get<string[]>(
      environment.apiUrl + '/access/activities',
      {
        params: params
      }
    );
  }

}
