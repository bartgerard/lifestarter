import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Heartbeat} from '../model/heartbeat';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor(
    private http: HttpClient
  ) {
  }

  ping(): Observable<Heartbeat> {
    return this.http.get<Heartbeat>(environment.apiUrl + '/events/ping');
  }

  /**
   * Live feed of RSVP notifications. The caller owns the returned source and must close() it.
   */
  stream(): EventSource {
    return new EventSource(environment.apiUrl + '/events/stream');
  }

}
