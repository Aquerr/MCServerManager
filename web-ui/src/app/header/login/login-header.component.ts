import { Component } from '@angular/core';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-header-login',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './login-header.component.html',
  styleUrl: './login-header.component.css'
})
export class LoginHeaderComponent {

    isLoggedIn() {
      return false;
    }

  getCurrentUser() {
    return "";
  }
}
