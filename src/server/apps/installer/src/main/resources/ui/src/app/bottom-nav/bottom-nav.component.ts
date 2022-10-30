import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ActiveStageGuard} from "../active-stage-guard";
import {RouterService} from "../router.service";
import {Router} from "@angular/router";
import {InstallerService} from "../installer.service";
import {PopupDialogComponent} from "../popup-dialog/popup-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {CanActivateExitPage} from "../installer-can-activate";

@Component({
  selector: 'app-bottom-nav',
  templateUrl: './bottom-nav.component.html',
  styleUrls: ['./bottom-nav.component.scss']
})
export class BottomNavComponent implements OnInit {

  @Input() parentForm: FormGroup;
  @Input() showExitButton: boolean = true;
  @Input() submitButtonText: string = "NEXT";
  @Input() exitButtonText: string = "CANCEL";
  @Input() showBackButton: boolean = true;
  @Input() showSubmitButton: boolean = true;
  @Output() installerFormSubmit = new EventEmitter();
  disableSubmitButton: boolean = false;
  dialogRef: MatDialogRef<PopupDialogComponent>;

  constructor(private router: Router, private routerService: RouterService, private activeStageGuard: ActiveStageGuard,
              private installerService: InstallerService, private dialog: MatDialog,
              private canActivateExitPage: CanActivateExitPage) { }

  ngOnInit() {
    this.parentForm && this.parentForm.valueChanges
      .subscribe(() => {
      this.disableSubmitButton = this.parentForm.invalid;
    });
  }

  submit() {
    // components can either subscribe to this event or include bottom nav component in a form and do a form submit.
    // navigation to next page is handled in respective parent components
    this.installerFormSubmit.emit();
    setTimeout(() => {
      this.disableSubmitButton = true;
    }, 10);
  }

  navigateBack() {
    this.routerService.navigateBack();
  }

  exit(){
    this.openDialog("Confirm to exit Control Center Installation Wizard", "confirm");
  }

  openDialog(msg: string, status: string): void {
    if (this.dialogRef){
      this.dialogRef.close();
    }
    this.dialogRef = this.dialog.open(PopupDialogComponent, {
      width: '341px',
      height: '167px',
      data: {msg: msg, status: status, confirmDialog: true},
      autoFocus: false
    });
    this.dialogRef.afterClosed().subscribe(async result => {
      if(result) {
        this.activeStageGuard.clearState();
        this.installerService.setConfig(null);
        this.canActivateExitPage.installExited = true;
        this.router.navigateByUrl('exit');
        await this.installerService.exit().toPromise();
      }
    });
  }
}
