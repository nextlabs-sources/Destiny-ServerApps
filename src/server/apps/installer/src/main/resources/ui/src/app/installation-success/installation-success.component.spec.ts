import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstallationSuccessComponent } from './installation-success.component';

describe('SuccessComponent', () => {
  let component: InstallationSuccessComponent;
  let fixture: ComponentFixture<InstallationSuccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstallationSuccessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
