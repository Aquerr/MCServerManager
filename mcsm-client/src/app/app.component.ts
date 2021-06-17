import {AfterContentInit, Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.less']
})
export class AppComponent implements AfterContentInit {
  title = 'mcsm-client';

  ngAfterContentInit(): void {

    console.log("Registering cards...");

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
