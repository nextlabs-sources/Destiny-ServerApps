import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SslCertUploadComponent } from './ssl-cert-upload.component';

describe('SslCertUploadComponent', () => {
  let component: SslCertUploadComponent;
  let fixture: ComponentFixture<SslCertUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SslCertUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SslCertUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
