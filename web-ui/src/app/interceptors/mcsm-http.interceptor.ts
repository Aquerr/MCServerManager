import {Injectable} from '@angular/core';
import {Observable, tap} from 'rxjs';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../service/auth.service';
import {MaskService} from '../service/mask.service';
import {NotificationService} from '../service/notification.service';

@Injectable()
export class McsmHttpInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService,
              private maskService: MaskService,
              private notificationService: NotificationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authState = this.authService.getAuthState();
    if (authState?.username && authState.jwt) {
      request = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + authState.jwt)
      });
    }

    return next.handle(request).pipe(tap(error => console.log(error)), tap(
      () => {},
      error => {
        if (error instanceof HttpErrorResponse) {
          if (error.status === 401) {
            this.maskService.hide();
            this.notificationService.warningNotification("You are not authorized.", "Unauthorized");
          } else if (error.status === 404) {
            this.maskService.hide();
            this.notificationService.errorNotification("Resource has not been found.", "Not found");
          } else if (error.status === 500) {
            this.maskService.hide();
            this.notificationService.errorNotification("An error occurred on the server.", "Server error");
          } else {
            console.warn(error.message);
          }
        }
      }));
  }

}
