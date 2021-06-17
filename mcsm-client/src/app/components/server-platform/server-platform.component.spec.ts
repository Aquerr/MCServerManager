import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerPlatformComponent } from './server-platform.component';

describe('ServerPlatformComponent', () => {
  let component: ServerPlatformComponent;
  let fixture: ComponentFixture<ServerPlatformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServerPlatformComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServerPlatformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
