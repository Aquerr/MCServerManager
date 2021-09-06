import { Component, OnInit } from '@angular/core';
import { PlatformService } from "../../services/platform.service";

@Component({
  selector: 'app-server-platform',
  templateUrl: './server-platform.component.html',
  styleUrls: ['./server-platform.component.less']
})
export class ServerPlatformComponent implements OnInit {

  platforms = this.platformService.getPlatforms();

  constructor(private platformService: PlatformService) {

  }

  ngOnInit(): void {

  }

}
