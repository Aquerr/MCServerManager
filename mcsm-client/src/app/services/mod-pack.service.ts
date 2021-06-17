import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Category } from "../model/category";
import {environment} from "../../environments/environment";
import {ModPack} from "../model/modpacks";

@Injectable({
  providedIn: 'root'
})
export class ModPackService {

  constructor(private http: HttpClient) { }

  getModpacks(categoryId: number, size: number, version: string, startFromIndex: number, modPackName: string) {
    return this.http.get<ModPack[]>(environment.API_URL + `/modpacks/search?categoryId=${categoryId}&size=${size}&version=${version}&index=${startFromIndex}&modpackName=${modPackName}`);
  }

  getCategories() {
    return this.http.get<Category[]>(environment.API_URL + '/modpacks/categories');
  }

  getVersions() {
    return this.http.get<string[]>(environment.API_URL + "/modpacks/versions");
  }

  getModpackDescription(modpackId: number) {
    return this.http.get(environment.API_URL + `/modpacks/${modpackId}/description`, {responseType: 'text'});
  }
}
