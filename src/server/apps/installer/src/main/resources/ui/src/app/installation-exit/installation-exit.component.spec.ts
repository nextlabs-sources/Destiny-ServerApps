import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstallationExitComponent } from './installation-exit.component';

describe('InstallationExitComponent', () => {
  let component: InstallationExitComponent;
  let fixture: ComponentFixture<InstallationExitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstallationExitComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationExitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
