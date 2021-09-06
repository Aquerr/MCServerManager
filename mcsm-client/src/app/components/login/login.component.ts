import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../services/token-storage.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less']
})
export class LoginComponent implements OnInit {
  credentials = {username: '', password: ''};
  error: any;

  isLoggedIn = false;
  isLoginFailed = false;

  constructor(private tokenStorageService: TokenStorageService, private authServie: AuthService, private http: HttpClient, private router: Router) {

  }

  ngOnInit(): void {
    if (this.tokenStorageService.getToken()) {
      this.isLoggedIn = true;
    }
  }

  login(): void {
    this.authServie.authenticate(this.credentials).subscribe(data => {

      console.log(data);
      this.tokenStorageService.saveToken(data.jwt);
      this.tokenStorageService.saveUser(data);

      this.isLoginFailed = false;
      this.isLoggedIn = true;

      this.router.navigateByUrl('/');
    }, err => {
      this.error = err.error.message;
      this.isLoginFailed = true;
    });
  }

}
