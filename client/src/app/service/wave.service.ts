import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
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

  waves(): Observable<Wave[]> {
    return this.http.get<Wave[]>(environment.apiUrl + '/waves');
  }

  /**
   * The wave still accepting replies, or null once every deadline has passed.
   */
  currentWave(): Observable<Wave | null> {
    return this.http
      .get<Wave>(environment.apiUrl + '/waves/current')
      .pipe(catchError((error: HttpErrorResponse) => error.status === 404 ? of(null) : throwError(error)));
  }

}
