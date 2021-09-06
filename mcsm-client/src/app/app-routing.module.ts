import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ServerPlatformComponent} from "./components/server-platform/server-platform.component";
import { ServerListComponent } from "./components/server-list/server-list.component";
import {AddServerComponent} from "./components/add-server/add-server.component";
import {LoginComponent} from "./components/login/login.component";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'servers'},
  { path: 'servers', component: ServerListComponent },
  { path: 'servers/add-server', component: AddServerComponent },
  { path: 'servers/server-platform', component: ServerPlatformComponent },
  { path: 'login', component: LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
