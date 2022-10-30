import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagementServerConfigComponent } from './management-server-config.component';

describe('ManagementServerConfigComponent', () => {
  let component: ManagementServerConfigComponent;
  let fixture: ComponentFixture<ManagementServerConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManagementServerConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManagementServerConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
