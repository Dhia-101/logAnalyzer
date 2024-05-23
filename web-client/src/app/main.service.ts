import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MainService {

  constructor(private http: HttpClient) { }

  getLogLevelsData() {
    return this.http.get('http://localhost:49152/metrics');
  }

}
