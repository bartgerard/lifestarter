import {Injectable} from '@angular/core';
import {Pledge} from '../model/pledge';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {WebConfig} from '../model/web-config';

@Injectable({
  providedIn: 'root'
})
export class PledgeService {

  constructor(
    private http: HttpClient
  ) {
  }

  pledges(): Observable<Pledge[]> {
    return this.http.get<Pledge[]>(WebConfig.baseUri + '/pledges');
  }

}
