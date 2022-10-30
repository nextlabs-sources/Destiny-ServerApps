import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperUserCredentialsComponent } from './super-user-credentials.component';

describe('SuperUserCredentialsComponent', () => {
  let component: SuperUserCredentialsComponent;
  let fixture: ComponentFixture<SuperUserCredentialsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuperUserCredentialsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuperUserCredentialsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
