import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstallationDirectoryComponent } from './installation-directory.component';

describe('InstallationDirectoryComponent', () => {
  let component: InstallationDirectoryComponent;
  let fixture: ComponentFixture<InstallationDirectoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstallationDirectoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationDirectoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
