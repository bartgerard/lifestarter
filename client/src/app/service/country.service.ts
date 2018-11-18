import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {WebConfig} from '../model/web-config';
import {Country} from '../model/country';

@Injectable({
  providedIn: 'root'
})
export class CountryService {

  constructor(
    private http: HttpClient
  ) {
  }

  countries(): Observable<Country[]> {
    return this.http.get<Country[]>(WebConfig.baseUri + '/countries');
  }

}
