import { Injectable } from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor(private snackBar: MatSnackBar) {

  }

  showToast(message: string, action: string) {
    this.snackBar.open(message, action, {duration: 3000});
  }
}
