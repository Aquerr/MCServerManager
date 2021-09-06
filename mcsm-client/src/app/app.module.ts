import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import {AppComponent} from './app.component';
import { ServerPlatformComponent } from './components/server-platform/server-platform.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { ServerListComponent } from './components/server-list/server-list.component';
import { AddServerComponent } from './components/add-server/add-server.component';
import { AddServerForgeComponent } from './components/add-server/forge/add-server-forge.component';
import {FormsModule} from "@angular/forms";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatDialogModule} from "@angular/material/dialog";
import {MatSliderModule} from "@angular/material/slider";
import { McsmModal } from './components/modal/mcsm-modal.component';
import {MatButtonModule} from "@angular/material/button";
import { LoginComponent } from './components/login/login.component';
import {AuthInterceptor} from "./interceptor/auth.interceptor";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { SpinnerComponent } from './components/spinner/spinner.component';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatCardModule} from "@angular/material/card";
import {MatRippleModule} from "@angular/material/core";
import {LoadingInterceptor} from "./interceptor/loading.interceptor";
import {MatSelectModule} from "@angular/material/select";
import {MatTreeModule} from "@angular/material/tree";

@NgModule({
  declarations: [
    AppComponent,
    ServerPlatformComponent,
    ServerListComponent,
    AddServerComponent,
    AddServerForgeComponent,
    McsmModal,
    LoginComponent,
    SpinnerComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
        BrowserAnimationsModule,
        MatDialogModule,
        MatSliderModule,
        MatButtonModule,
        MatListModule,
        MatIconModule,
        MatExpansionModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatCardModule,
        MatRippleModule,
        MatSelectModule,
        MatTreeModule
    ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
