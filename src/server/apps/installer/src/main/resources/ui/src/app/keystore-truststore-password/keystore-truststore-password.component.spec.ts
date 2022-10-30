import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KeystoreTruststorePasswordComponent } from './keystore-truststore-password.component';

describe('KeystoreTruststorePasswordComponent', () => {
  let component: KeystoreTruststorePasswordComponent;
  let fixture: ComponentFixture<KeystoreTruststorePasswordComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KeystoreTruststorePasswordComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KeystoreTruststorePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
