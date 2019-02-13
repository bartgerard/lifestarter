import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Registration} from '../model/registration';
import {Observable} from 'rxjs';

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
    return this.http.get<number>(environment.serverUrl + '/registrations/guests');
  }

}
