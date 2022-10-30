import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreInstallSummaryComponent } from './pre-install-summary.component';

describe('PreInstallSummaryComponent', () => {
  let component: PreInstallSummaryComponent;
  let fixture: ComponentFixture<PreInstallSummaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PreInstallSummaryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreInstallSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
