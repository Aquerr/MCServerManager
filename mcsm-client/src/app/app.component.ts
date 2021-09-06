import {AfterContentInit, Component, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {McsmModal} from "./components/modal/mcsm-modal.component";
import {AuthService} from "./services/auth.service";
import {HttpClient} from "@angular/common/http";
import {
  Event,
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
  RouterEvent
} from "@angular/router";
import {TokenStorageService} from "./services/token-storage.service";
import {environment} from "../environments/environment";
import {LoadingService} from "./services/loading.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.less']
})
export class AppComponent implements AfterContentInit, OnInit {
  title = 'mcsm-client';
  username: any = this.tokenStorageService.getUser();
  loading: boolean = false;
  panelOpenState: boolean = false;

  constructor(private tokenStorageService: TokenStorageService,
              private dialog: MatDialog,
              private router: Router,
              private authService: AuthService,
              private http: HttpClient,
              public loadingService: LoadingService) {
    this.authService.authenticate(undefined);
    this.router.events.subscribe((e: Event) => {
      this.navigationInterceptor(e);
    });
  }

  ngOnInit(): void {
    let token = this.tokenStorageService.getToken();

    if (token) {
      const user = this.tokenStorageService.getUser();
      this.username = user.username;
    }
  }

  ngAfterContentInit(): void {

    console.log("Registering cards...");

  }

  openDialog() {
    const dialogRef = this.dialog.open(McsmModal);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  isLoggedIn() {
    const token = this.tokenStorageService.getToken();
    console.log("Is logged in? " + (!!token));
    return token;
    // return this.authService.authenticated;
  }

  logout(): void {
    this.http.post(environment.API_URL + '/auth/logout', {}).subscribe(() => {
      this.tokenStorageService.signOut();
      this.authService.authenticated = false;
      window.location.reload();
    });
  }

  private navigationInterceptor(event: Event) {
    if (event instanceof NavigationStart)
    {
      this.loading = true;
    }
    if(event instanceof NavigationEnd) {
      this.loading = false;
    }
    if (event instanceof NavigationError) {
      this.loading = false;
    }
    if (event instanceof NavigationCancel) {
      this.loading = false;
    }
    console.log("Loading: " + this.loading);
  }
}

// $(document).ready(function () {
//   console.log("Registering cards...");
//   $(".card").on("click", function (event) {
//     console.log("Clicked!");
//     let serverId = $(this).find("#server-id").val();
//     window.location.href = "/servers/" + serverId;
//   });
//   $("#import-server-button").on("click", function() {
//     $("#server-import-modal").modal("show");
//   });
//   $("#import-server").on("click", function() {
//     var data = {};
//     data["server-name"] = $("#server-name").val();
//     data["path"] = $("#path").val();
//     data["platform"] = $("#platform").val();
//     $.ajax({
//       url: "/api/servers/import-server",
//       method: "POST",
//       data: JSON.stringify(data),
//       contentType: "application/json",
//       success: function (response) {
//         location.reload();
//       },
//       error: function (response) {
//         alert(response.responseJSON.message)
//         console.log(response);
//         console.log("Error: " + response.responseJSON.message);
//       }
//     });
//   });
// });
