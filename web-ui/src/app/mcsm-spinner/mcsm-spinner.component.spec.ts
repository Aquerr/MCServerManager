import { ComponentFixture, TestBed } from '@angular/core/testing';

import { McsmSpinnerComponent } from './mcsm-spinner.component';

describe('McsmSpinnerComponent', () => {
  let component: McsmSpinnerComponent;
  let fixture: ComponentFixture<McsmSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [McsmSpinnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(McsmSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
