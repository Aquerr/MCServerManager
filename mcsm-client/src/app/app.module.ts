import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ServerPlatformComponent } from './components/server-platform/server-platform.component';
import {HttpClientModule} from "@angular/common/http";
import { ServerListComponent } from './components/server-list/server-list.component';
import { AddServerComponent } from './components/add-server/add-server.component';
import { AddServerForgeComponent } from './components/add-server/forge/add-server-forge.component';
import {FormsModule} from "@angular/forms";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatDialogModule} from "@angular/material/dialog";

@NgModule({
  declarations: [
    AppComponent,
    ServerPlatformComponent,
    ServerListComponent,
    AddServerComponent,
    AddServerForgeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    MatDialogModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
