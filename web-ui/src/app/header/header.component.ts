import { Component } from '@angular/core';
import {TimeHeaderComponent} from './time/time-header.component';
import {LoginHeaderComponent} from './login/login-header.component';
import {LogoutHeaderComponent} from './logout/logout-header.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    TimeHeaderComponent,
    LoginHeaderComponent,
    LogoutHeaderComponent,
    NgIf
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

    isAuthenticated() {
        return false;
    }
}
