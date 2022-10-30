import {Component, OnInit} from '@angular/core';
import {RouterService} from "../router.service";
import {InstallerService} from "../installer.service";
import {Config} from "../../models/config";
import {DBConfig} from "../../models/db-config";
import {PortConfig} from "../../models/port-config";
import {ActiveStageGuard} from "../active-stage-guard";
import {ManagementServerConfig} from "../../models/management-server-config";

@Component({
  selector: 'app-pre-install-summary',
  templateUrl: './pre-install-summary.component.html',
  styleUrls: ['./pre-install-summary.component.scss']
})
export class PreInstallSummaryComponent implements OnInit {

  config: Config = new Config();
  portConfig: PortConfig = new PortConfig();
  dbConfig: DBConfig = new DBConfig();
  managementServerConfig: ManagementServerConfig = new ManagementServerConfig();
  typeDesc: string;

  constructor(private installerService: InstallerService, private routerService: RouterService, private activeStageGuard: ActiveStageGuard) {
  }

  async ngOnInit() {
    this.config = await this.installerService.getConfig();
    this.portConfig = this.config.port;
    this.managementServerConfig = this.config.managementServer;
    if(this.config.db.url){
      this.dbConfig = await this.installerService.getDbDetails(this.config.db.url).toPromise();
    } else {
      this.dbConfig = this.config.db;
    }
    this.dbConfig.username = this.config.db.username;
    let type = await this.installerService.getType();
    if (type === "COMPLETE") {
      this.typeDesc = "Complete Server";
    } else if (type === "MANAGEMENT_SERVER") {
      this.typeDesc = "Management Server";
    } else if (type === "ICENET") {
      this.typeDesc = "ICENet Server";
    }
  }

  isIceNetInstall(): boolean {
    return this.config.type === 'ICENET';
  }

  isUpgrade(): boolean {
    return this.config.runningMode == "UPGRADE";
  }

  async formSubmit() {
    await this.installerService.installControlCentre().toPromise();
    this.routerService.navigateNext();
  }
}
