import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {McsmHttpInterceptor} from './interceptors/mcsm-http.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes),
    {provide: HTTP_INTERCEPTORS, useClass: McsmHttpInterceptor, multi: true}]
};
