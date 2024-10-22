import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsJavaComponent } from './settings-java.component';

describe('JavaComponent', () => {
  let component: SettingsJavaComponent;
  let fixture: ComponentFixture<SettingsJavaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsJavaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsJavaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
