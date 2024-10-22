import {Component, OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-header-time',
  standalone: true,
  imports: [
    DatePipe
  ],
  templateUrl: './time-header.component.html',
  styleUrl: './time-header.component.css'
})
export class TimeHeaderComponent implements OnInit {
  currentTime: Date = new Date();

  ngOnInit(): void {
    setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }
}
