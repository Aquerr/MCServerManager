import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerFilesComponent } from './server-files.component';

describe('FilesComponent', () => {
  let component: ServerFilesComponent;
  let fixture: ComponentFixture<ServerFilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServerFilesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
