import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Registration} from '../model/registration';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(
    private http: HttpClient
  ) {
  }

  register(
    registration: Registration
  ) {
    this.http.put(environment.serverUrl + '/registrations', registration)
      .subscribe(result => {
        console.log(result);
      });
  }

}
