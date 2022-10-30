import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OsAuthConfigMsgComponent } from './os-auth-config-msg.component';

describe('OsAuthConfigMsgComponent', () => {
  let component: OsAuthConfigMsgComponent;
  let fixture: ComponentFixture<OsAuthConfigMsgComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OsAuthConfigMsgComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OsAuthConfigMsgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
