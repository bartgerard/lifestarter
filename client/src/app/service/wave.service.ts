import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Wave} from '../model/wave';

@Injectable({
  providedIn: 'root'
})
export class WaveService {

  constructor(
    private http: HttpClient
  ) {
  }

  currentWave(): Observable<Wave> {
    return this.http.get<Wave>(environment.serverUrl + '/waves/current');
  }

}
