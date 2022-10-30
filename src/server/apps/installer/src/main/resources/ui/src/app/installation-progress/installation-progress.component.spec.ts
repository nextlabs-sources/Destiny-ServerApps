import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstallationProgressComponent } from './installation-progress.component';

describe('InstallationProgressComponent', () => {
  let component: InstallationProgressComponent;
  let fixture: ComponentFixture<InstallationProgressComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstallationProgressComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
