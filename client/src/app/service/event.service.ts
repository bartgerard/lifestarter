import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Info} from "../model/info";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor(
    private http: HttpClient
  ) {
  }

  getPing(): Observable<Info> {
    return this.http.get<Info>('http://localhost:8080/feed');
  }

}
