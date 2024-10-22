import { Injectable } from '@angular/core';
import {AuthState} from '../model/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private static readonly AUTH_KEY: string = 'mcsm-auth';

  constructor() { }

  login() {
    //TODO: Invoke backend auth service.
    //TODO: Store jwt inside mcsm-auth.
  }

  getAuthState(): AuthState | null {
    const authState = sessionStorage.getItem(AuthService.AUTH_KEY);
    if (!authState)
      return null;

    return JSON.parse(authState);
  }
}
