import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerPanelComponent } from './server-panel.component';

describe('ServerPanelComponent', () => {
  let component: ServerPanelComponent;
  let fixture: ComponentFixture<ServerPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServerPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
