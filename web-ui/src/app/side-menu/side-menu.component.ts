import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgIf, NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'app-side-menu',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    NgTemplateOutlet
  ],
  templateUrl: './side-menu.component.html',
  styleUrl: './side-menu.component.css'
})
export class SideMenuComponent {
  @Output() changeThemeEmit = new EventEmitter<unknown>();
  @Input() darkMode!: boolean;
  isMobile: boolean = false;
  version: string = '';
}
