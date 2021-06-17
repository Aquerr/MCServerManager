import {Component, HostListener, OnInit} from '@angular/core';
import { ModPack } from 'src/app/model/modpacks';
import {ModPackService} from "../../../services/mod-pack.service";
import {MatDialog} from "@angular/material/dialog";
import {ServerListComponent} from "../../server-list/server-list.component";

@Component({
  selector: 'app-add-server-forge',
  templateUrl: './add-server-forge.component.html',
  styleUrls: ['./add-server-forge.component.less']
})
export class AddServerForgeComponent implements OnInit {

  categories = this.modPackService.getCategories();
  versions = this.modPackService.getVersions();
  modpacks : ModPack[] = [];

  selectedVersion = "";
  selectedCategoryId = 0;
  searchedModPackName = "";

  constructor(private dialog:MatDialog, private modPackService:ModPackService) { }

  ngOnInit(): void {

    this.modPackService.getModpacks(0, 20, '', 0, this.searchedModPackName).subscribe(data => this.modpacks = data);
  }

  loadModpacks(): void {
    this.modPackService.getModpacks(this.selectedCategoryId, 20, this.selectedVersion, 0, this.searchedModPackName)
      .subscribe(data => this.modpacks = data);
  }

  @HostListener('window:scroll', ['$event'])
  onScroll(): void {
    if (document.body.scrollHeight == window.scrollY + window.innerHeight) {
      console.log("Selected Version: " + this.selectedVersion);
      console.log("Selected Category: " + this.selectedCategoryId);
      console.log("Modpack count: " + this.modpacks.length);

      this.modPackService.getModpacks(this.selectedCategoryId, 20, this.selectedVersion, this.modpacks.length, this.searchedModPackName)
        .subscribe(data =>
          data.forEach(modpack => {
            this.modpacks.push(modpack);
          }));
    }
  }

  onVersionChange(value: string): void {
    this.selectedVersion = value;
    console.log("Changed version to: " + this.selectedVersion);
    this.loadModpacks();
  }

  onCategoryChange(value: number): void {
    this.selectedCategoryId = value;
    console.log("Changed category to: " + this.selectedCategoryId);
    this.loadModpacks();
  }

  // onNameChange(value: string): void {
  //   this.searchedModPackName = value;
  //   console.log("Changed searched name to: " + value);
  //   this.loadModpacks();
  // }

  selectServerVersion(value: string): void {

  }

  // $(document).ready(function () {
  //
  //   function registerCardClickEvent(card) {
  //     $(card).on("click", function () {
  //       // $(this).on("click", function () {
  //       let modpackTitle = $(this).find(".card-title").text();
  //       let modpackId = $(this).find(".modpack-id").val();
  //
  //       $.ajax({
  //         url: "/api/modpacks/" + modpackId + "/description",
  //         method: "GET"
  //       }).done(function (description) {
  //         $("#modpack-modal .modal-title").text(modpackTitle);
  //         $("#modpack-modal .modal-body").html(description);
  //         $("#modpack-modal #select-server-version").attr("modpack-id", modpackId);
  //         $("#modpack-modal").modal("show");
  //       });
  //     });
  //   }
  //
  //   for (const card of $(".card")) {
  //     registerCardClickEvent(card);
  //   }
  //
  //   $("#modpack-name-search").on("keydown", function(event){
  //     if (event.keyCode == 13) {
  //       let version = $("#version-filter").val();
  //       let categoryId = $("#category-filter").val();
  //       let modpackName = $("#modpack-name-search").val();
  //
  //       $.ajax({
  //         url: "/api/modpacks/search?categoryId=" + categoryId + "&size=20&version=" + version + "&index=0" + "&modpackName=" + modpackName,
  //         method: "GET",
  //         success: function (response) {
  //           console.log(response);
  //           $("#modpacks-list").empty();
  //           appendModpacksToList(response);
  //         }
  //       })
  //     }
  //   });
  //
  //   $("#select-server-version").on("click", function () {
  //
  //     let modpackId = $("#select-server-version").attr("modpack-id");
  //     $("#modpack-modal").modal("hide");
  //
  //     let selectServerVersionModal = $("#server-version-modal");
  //     selectServerVersionModal.find("#install-server").attr("modpack-id", modpackId);
  //     selectServerVersionModal.modal("show");
  //
  //     $("#modpack-modal #select-server-version").attr("modpack-id", modpackId);
  //
  //     console.log("Getting avaialble server packs for modpack id = " + modpackId);
  //     $.ajax({
  //       url: "/api/modpacks/" + modpackId + "/serverpacks",
  //       method: "GET",
  //       success: function (response) {
  //         console.log(response);
  //         let serverVersionsList = $("#server-versions-list");
  //         serverVersionsList.empty();
  //         for (const serverpack of response) {
  //           serverVersionsList.append(new Option(serverpack["name"], serverpack["id"]));
  //         }
  //       }
  //     });
  //   });
  //
  //   $("#install-server").on("click", function () {
  //     $("#server-version-modal").modal("hide");
  //     $("#server-install-modal").modal("show");
  //
  //     let installationStatusMessageInterval;
  //     let modpackId = $("#install-server").attr("modpack-id");
  //     let serverPackId = $("#server-versions-list").val();
  //
  //     $("#server-install-modal #open-server").hide();
  //     $("#server-install-modal .modal-body").html("                <div class=\"text-center\">\n" +
  //       "                    <p>Your modpack is being installed. Please wait!</p>\n" +
  //       "                    <div class=\"loader\" role=\"status\">\n" +
  //       "                        <span class=\"sr-only\">Loading...</span>\n" +
  //       "                    </div>\n" +
  //       "                    <p id=\"installation-status\"></p>\n" +
  //       "                </div>");
  //
  //     //TODO: Fire installation on the server...
  //     $.ajax({
  //       url: "/api/modpacks/" + modpackId + "/serverpacks/" + serverPackId + "/install",
  //       method: "POST",
  //       success: function (response) {
  //         console.log(response);
  //         clearInterval(installationStatusMessageInterval);
  //         $("#server-install-modal .modal-body").text("Your server is ready!");
  //         $("#server-install-modal #open-server").show();
  //         $("#server-install-modal #open-server").attr("href", "/servers/" + response);
  //       },
  //       error: function (errorResponse) {
  //         console.log(errorResponse);
  //         clearInterval(installationStatusMessageInterval);
  //         $("#server-install-modal .modal-body").text(errorResponse["responseJSON"]["message"]);
  //       }
  //     });
  //
  //     function showInstallationStatus() {
  //       //TODO: Show installation status for the given user.
  //       $.ajax({
  //         url: "/api/servers/installation-status/" + modpackId,
  //         method: "GET"
  //       }).done(function (response) {
  //         let message = response["message"];
  //         let percent = response["percent"];
  //         $("#server-install-modal #installation-status").text(percent + " " + message);
  //       });
  //     }
  //
  //     //TODO: Show installation status for the user
  //     installationStatusMessageInterval = setInterval(showInstallationStatus, 4000);
  //   });
  //


  onModpackCardClick(modPack: ModPack) {
    this.modPackService.getModpackDescription(modPack.id).subscribe(data => {
      console.log("Card clicked!");

      let dialogRef = this.dialog.open(ServerListComponent, {
        height: '400px',
        width: '600px',
      });
    });

    //       let modpackTitle = $(this).find(".card-title").text();
    //       let modpackId = $(this).find(".modpack-id").val();
    //
    //       $.ajax({
    //         url: "/api/modpacks/" + modpackId + "/description",
    //         method: "GET"
    //       }).done(function (description) {
    //         $("#modpack-modal .modal-title").text(modpackTitle);
    //         $("#modpack-modal .modal-body").html(description);
    //         $("#modpack-modal #select-server-version").attr("modpack-id", modpackId);
    //         $("#modpack-modal").modal("show");
    //       });
  }
}
