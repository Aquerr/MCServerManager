import { Component, OnInit } from '@angular/core';
import {Server} from "../../model/server";
import {ServerService} from "../../services/server.service";
import {PlatformService} from "../../services/platform.service";
import {Observable} from "rxjs";
import {Platform} from "../../model/platform";

@Component({
  selector: 'app-server-list',
  templateUrl: './server-list.component.html',
  styleUrls: ['./server-list.component.less']
})
export class ServerListComponent implements OnInit {

  servers: Server[] = [];
  platforms: Platform[] = [];

  constructor(private serverService: ServerService, private platformService: PlatformService) {

  }

  ngOnInit(): void {
    this.serverService.getServers().subscribe(servers => this.servers = servers);
    this.platformService.getPlatforms().subscribe(platforms => this.platforms = platforms);
    console.log("Server list loaded!");
  }

  showServer(id: number) {
    console.log("Show server for id = " + id);
  }

  getImagePathForPlatform(platform: string) : string {
    // @ts-ignore
    return this.platforms
      .find((element) => element.name.toUpperCase() == platform.toUpperCase())
      .imagePath || "";
  }
}
