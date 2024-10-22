import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeHeaderComponent } from './time-header.component';

describe('TimeComponent', () => {
  let component: TimeHeaderComponent;
  let fixture: ComponentFixture<TimeHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TimeHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
