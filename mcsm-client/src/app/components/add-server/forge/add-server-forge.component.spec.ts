import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddServerForgeComponent } from './add-server-forge.component';

describe('AddServerForgeComponent', () => {
  let component: AddServerForgeComponent;
  let fixture: ComponentFixture<AddServerForgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddServerForgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddServerForgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
