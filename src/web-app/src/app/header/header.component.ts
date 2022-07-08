import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  isAuthenticated(): boolean {
    return true;
  }

  hasRole(role: string): boolean {
    return true;
  }

  getUsername(): string {
    return 'Bogdan';
  }
}
