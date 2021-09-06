import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Server} from "../model/server";
import {Observable, of} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {ToastService} from "./toast.service";

@Injectable({
  providedIn: 'root'
})
export class ServerService {

  constructor(private toastService: ToastService, private http: HttpClient) { }


  getServers(): Observable<Server[]> {
    return this.http.get<Server[]>(environment.API_URL + '/servers')
      .pipe(
        tap(() => this.log('Fetched servers')),
        catchError(this.handleError<Server[]>('Fetching server list', [])));
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  private log(s: string) {
    console.log(s);
    this.toastService.showToast(s, "");
  }
}
