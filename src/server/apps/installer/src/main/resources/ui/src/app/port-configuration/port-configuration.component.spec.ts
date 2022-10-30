import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PortConfigurationComponent } from './port-configuration.component';

describe('PortConfigurationComponent', () => {
  let component: PortConfigurationComponent;
  let fixture: ComponentFixture<PortConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PortConfigurationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PortConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
