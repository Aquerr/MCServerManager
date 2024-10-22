import { Routes } from '@angular/router';
import {HomeComponent} from './pages/home/home.component';
import {AuthGuard} from './guard/auth.guard';
import {inject} from '@angular/core';
import {LoginComponent} from './pages/login/login.component';
import {PlatformsComponent} from './pages/platforms/platforms.component';
import {ServerPanelComponent} from './pages/server-panel/server-panel.component';
import {SettingsComponent} from './pages/settings/settings.component';
import {SettingsJavaComponent} from './pages/settings/java/settings-java.component';
import {SettingsUsersComponent} from './pages/settings/users/settings-users.component';
import {SettingsLogsComponent} from './pages/settings/logs/settings-logs.component';
import {ServerFilesComponent} from './pages/server-panel/files/server-files.component';
import {ModpacksComponent} from './pages/modpacks/modpacks.component';

export const routes: Routes = [
  {path: 'home', component: HomeComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()]},
  {path: 'login', component: LoginComponent},
  {path: 'platforms', component: PlatformsComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()]},
  {path: 'modpacks', component: ModpacksComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()]},
  {path: 'server-panel/:id', component: ServerPanelComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()]},
  {path: 'server-panel/:id/files', component: ServerFilesComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()]},
  {path: 'settings', component: SettingsComponent, canActivate: [() => inject(AuthGuard).isAuthenticated()], children: [
      {path: 'java', component: SettingsJavaComponent},
      {path: 'users', component: SettingsUsersComponent},
      {path: 'logs', component: SettingsLogsComponent}
  ]},
  {path: '', redirectTo: "/home", pathMatch: "full"},
  {path: "**", redirectTo: 'home'}
];
