import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Config} from "../../models/config";
import {InstallerService} from "../installer.service";

@Component({
  selector: 'app-os-auth-config-msg',
  templateUrl: './os-auth-config-msg.component.html',
  styleUrls: ['./os-auth-config-msg.component.scss']
})
export class OsAuthConfigMsgComponent implements OnInit, AfterViewInit {

  config: Config;

  constructor(private installerService: InstallerService) { }

  ngOnInit() {
  }

  async ngAfterViewInit() {
    this.config =  await this.installerService.getConfig();
  }

  isIcenetInstall() {
    return this.config && this.config.type === "ICENET";
  }

}
