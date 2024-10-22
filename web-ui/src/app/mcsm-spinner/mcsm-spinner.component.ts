import { Component } from '@angular/core';
import {NgxSpinnerComponent} from 'ngx-spinner';

@Component({
  selector: 'app-mcsm-spinner',
  standalone: true,
  imports: [
    NgxSpinnerComponent
  ],
  templateUrl: './mcsm-spinner.component.html',
  styleUrl: './mcsm-spinner.component.css'
})
export class McsmSpinnerComponent {

}
