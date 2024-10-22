import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {SideMenuComponent} from './side-menu/side-menu.component';
import {NgIf, NgTemplateOutlet} from '@angular/common';
import {HeaderComponent} from './header/header.component';
import {McsmSpinnerComponent} from './mcsm-spinner/mcsm-spinner.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SideMenuComponent, NgTemplateOutlet, HeaderComponent, McsmSpinnerComponent, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  isMobile: boolean = false;
  version: string = "";

  changeTheme() {

  }

  isDarkMode() {
    return false;
  }
}
