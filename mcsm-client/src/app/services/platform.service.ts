import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Platform } from "../model/platform";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PlatformService {

  constructor(private http: HttpClient) { }


  getPlatforms() {
    // TODO: Get platforms from back-end
    return this.http.get<Platform[]>(environment.API_URL + '/platforms');

    // Convert to angular class


    // Return


  }
}
