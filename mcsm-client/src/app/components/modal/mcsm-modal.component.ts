import { Component } from '@angular/core';

@Component({
  selector: 'mcsm-modal',
  templateUrl: './mcsm-modal.component.html',
})
export class McsmModal {

  modalTitle: string = "";
  content: string = "";
  submitButtonValue: string = "Submit";
  submitButtonCallback = () => {};

  selectVisible: boolean = false;
  selectLabel: string = "";
  selectItems: any[] = [];
  selectItemValue = function (item: any): string {return ""};
  selectItemName = function (item: any): string {return ""};
  selectedItemValue: any;

  // modalActionElements: HTMLElement[] = [];
  // modalBodyElements: HTMLElement[] = [];
  // modalFieldsElements: HTMLElement[] = [];

  constructor() {

  }

  public setTitle(title: string) : void {
    this.modalTitle = title;
  }

  public setSubmitButtonCallback(callback: () => void) : void {
    this.submitButtonCallback = callback;
  }

  public setSubmitButtonValue(value: string) {
    this.submitButtonValue = value;
  }

  public setContent(content: string) : void {
    this.content = content;
    console.log("Content = " + this.content);
  }

  public setSelectItemValueCallback(callback: (item:any) => string) : void {
    this.selectItemValue = callback;
  }

  public setSelectItemNameCallback(callback: (item:any) => string) : void {
    this.selectItemName = callback;
  }

  public setSelectVisible(isVisible: boolean): void {
    this.selectVisible = isVisible;
  }

  public setSelectLabel(selectLabel: string): void {
    this.selectLabel = selectLabel;
  }

  public setSelectItems(selectItems: any[]): void {
    this.selectItems = selectItems;
  }

  public getSelectedItemValue(): any {
    console.log(this.selectedItemValue);
    return this.selectedItemValue;
  }

  // public addBodyElement(element: HTMLElement) : void {
  //   this.modalBodyElements.push(element);
  // }
  //
  // public addFieldElement(element: HTMLElement) : void {
  //   this.modalFieldsElements.push(element);
  // }
  //
  // public addActionElement(element: HTMLElement) : void {
  //   this.modalActionElements.push(element);
  // }
}
