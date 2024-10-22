import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogoutHeaderComponent } from './logout-header.component';

describe('LogoutComponent', () => {
  let component: LogoutHeaderComponent;
  let fixture: ComponentFixture<LogoutHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogoutHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LogoutHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
