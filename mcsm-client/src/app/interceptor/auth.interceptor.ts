import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {TokenStorageService} from "../services/token-storage.service";
import {catchError} from "rxjs/operators";
import {Router} from "@angular/router";

const TOKEN_HEADER_KEY = 'Authorization';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router, private tokenServiceStorage: TokenStorageService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    let authRequest = request;
    const token = this.tokenServiceStorage.getToken();
    if (token != null) {
      authRequest = request.clone({headers: request.headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token)});
    }

    return next.handle(authRequest).pipe(catchError((err: HttpErrorResponse) => {
      if (err.status == 401) {
        this.tokenServiceStorage.signOut();
        this.router.navigateByUrl("/login");
      }
      return throwError(err.message);
    }));
  }
}
