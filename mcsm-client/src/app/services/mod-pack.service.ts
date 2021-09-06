import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Category } from "../model/category";
import {environment} from "../../environments/environment";
import {ModPack} from "../model/modpacks";
import {Observable} from "rxjs";
import {ServerPack} from "../model/serverpack";

@Injectable({
  providedIn: 'root'
})
export class ModPackService {

  constructor(private http: HttpClient) { }

  getModpacks(categoryId: number, size: number, version: string, startFromIndex: number, modPackName: string): Observable<ModPack[]> {
    return this.http.get<ModPack[]>(environment.API_URL + `/modpacks/search?categoryId=${categoryId}&size=${size}&version=${version}&index=${startFromIndex}&modpackName=${modPackName}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(environment.API_URL + '/modpacks/categories');
  }

  getVersions(): Observable<string[]> {
    return this.http.get<string[]>(environment.API_URL + "/modpacks/versions");
  }

  getModpackDescription(modpackId: number): Observable<string> {
    return this.http.get(environment.API_URL + `/modpacks/${modpackId}/description`, {responseType: 'text'});
  }

  getServerPacks(modpackId: number): Observable<ServerPack[]> {
    return this.http.get<ServerPack[]>(environment.API_URL + `/modpacks/${modpackId}/serverpacks`, {responseType: "json"});
  }
}
