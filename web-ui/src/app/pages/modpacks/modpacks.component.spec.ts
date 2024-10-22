import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModpacksComponent } from './modpacks.component';

describe('ModpacksComponent', () => {
  let component: ModpacksComponent;
  let fixture: ComponentFixture<ModpacksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModpacksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModpacksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
