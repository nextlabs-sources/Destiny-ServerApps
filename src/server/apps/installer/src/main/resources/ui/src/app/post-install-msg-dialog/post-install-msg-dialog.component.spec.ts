import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PostInstallMsgDialogComponent } from './post-install-msg-dialog.component';

describe('OsAuthInfoDialogComponent', () => {
  let component: PostInstallMsgDialogComponent;
  let fixture: ComponentFixture<PostInstallMsgDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PostInstallMsgDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PostInstallMsgDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
