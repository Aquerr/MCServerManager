import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  private isLoading = new BehaviorSubject(false);
  observableIsLoading = this.isLoading.asObservable();

  constructor() {

  }

  setLoading(isLoading: boolean) {
    this.isLoading.next(isLoading);
  }
}
