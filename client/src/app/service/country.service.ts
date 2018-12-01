import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Country} from '../model/country';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CountryService {

  constructor(
    private http: HttpClient
  ) {
  }

  countries(): Observable<Country[]> {
    return this.http.get<Country[]>(environment.serverUrl + '/countries');
  }

}
