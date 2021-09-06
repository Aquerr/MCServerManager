import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  authenticated: boolean = false;

  constructor(private http: HttpClient) {

  }

  authenticate(credentials: any) : Observable<any> {
    return this.http.post(environment.API_URL + "/auth/login", credentials, httpOptions);
  }
}
