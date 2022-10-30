import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IcenetConfigMsgComponent } from './icenet-config-msg.component';

describe('IcenetConfigMsgComponent', () => {
  let component: IcenetConfigMsgComponent;
  let fixture: ComponentFixture<IcenetConfigMsgComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IcenetConfigMsgComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IcenetConfigMsgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
