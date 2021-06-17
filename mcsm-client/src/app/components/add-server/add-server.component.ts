import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-add-server',
  templateUrl: './add-server.component.html',
  styleUrls: ['./add-server.component.less']
})
export class AddServerComponent implements OnInit {

  constructor(public route: ActivatedRoute) { }

  ngOnInit(): void {

    // Prints chosen platform form url
    let platform = this.route.snapshot.queryParamMap.get("platform");

  }

}
