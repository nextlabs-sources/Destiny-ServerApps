import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {InstallerService} from "../installer.service";
import {Config} from "../../models/config";
import {IcenetConfigMsgComponent} from "../icenet-config-msg/icenet-config-msg.component";
import {OsAuthConfigMsgComponent} from "../os-auth-config-msg/os-auth-config-msg.component";

@Component({
  selector: 'post-install-msg-dialog',
  templateUrl: './post-install-msg-dialog.component.html',
  styleUrls: ['./post-install-msg-dialog.component.scss']
})
export class PostInstallMsgDialogComponent implements OnInit, AfterViewInit {

  config: Config;

  allSteps =
    {
      "icenetOnWindows": [
        {
          title: "Configure the ICENet server",
          component: IcenetConfigMsgComponent
        },
        {
          title: "If Management Server is configured to use OS Authentication for SQL Server, " +
            "configure <strong>Log On</strong> account for ICENet",
          component: OsAuthConfigMsgComponent
        }],
      "icenet": [
        {
          title: "Configure the ICENet server",
          component: IcenetConfigMsgComponent
        }],
      "osAuth": [
        {
          title: "Configure the Log On account for Control Center Service",
          component: OsAuthConfigMsgComponent
        }]
    };

  steps: any;

  constructor(public dialogRef: MatDialogRef<PostInstallMsgDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private installerService: InstallerService) { }

  ngOnInit() {
  }

  async ngAfterViewInit() {
    this.config =  await this.installerService.getConfig();
    if (this.isIcenetInstall() && this.isWindowsOS()) {
      this.steps = this.allSteps["icenetOnWindows"];
    }
    else if (this.isIcenetInstall()) {
      this.steps = this.allSteps["icenet"];
    } else if (this.isOSAuth()) {
      this.steps = this.allSteps["osAuth"];
    }
  }

  isIcenetInstall() {
    return this.config && this.config.type === "ICENET";
  }

  isOSAuth() {
    return this.config && this.config.db.osAuthentication;
  }

  isWindowsOS() {
    return this.config && this.config.operatingSystem == "WINDOWS";
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

}
