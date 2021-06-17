import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AppComponent} from "./app.component";
import {ServerPlatformComponent} from "./components/server-platform/server-platform.component";
import { ServerListComponent } from "./components/server-list/server-list.component";
import {AddServerComponent} from "./components/add-server/add-server.component";

const routes: Routes = [
  // { path: '', component: AppComponent},
  { path: '', component: ServerListComponent },
  { path: 'servers/add-server', component: AddServerComponent },
  { path: 'servers/server-platform', component: ServerPlatformComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
