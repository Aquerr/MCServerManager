import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginHeaderComponent } from './login-header.component';

describe('LoginComponent', () => {
  let component: LoginHeaderComponent;
  let fixture: ComponentFixture<LoginHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
