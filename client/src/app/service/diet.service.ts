import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {WebConfig} from "../model/web-config";

@Injectable({
  providedIn: 'root'
})
export class DietService {

  constructor(
    private http: HttpClient
  ) {
  }

  diets(): Observable<string[]> {
    return this.http.get<string[]>(WebConfig.baseUri + '/diets');
  }

}
