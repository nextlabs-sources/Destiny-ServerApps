import {Injectable} from "@angular/core";
import {InstallerService} from "./installer.service";
import {BehaviorSubject} from "rxjs";

export interface Stage {
  path: string;
  name: string
}

@Injectable({
  providedIn: 'root'
})
export class InstallerStages {

  constructor(private installerService: InstallerService) {
    this.iceNetInstallStagesSet
      .add('eula')
      .add('installation_type')
      .add('installation_directory')
      .add('management_server_configuration')
      .add('port_configuration')
      .add('summary')
      .add('installation');
    this.completeInstallStagesSet
      .add('eula')
      .add('installation_type')
      .add('license')
      .add('installation_directory')
      .add('ssl_certificates')
      .add('database_configuration')
      .add('superuser_credentials')
      .add('port_configuration')
      .add('key_trust_store_password')
      .add('summary')
      .add('installation');
    this.upgradeStagesSet
      .add('eula')
      .add('installation_directory')
      .add('summary')
      .add('installation');
    this.installStagesSubject = new BehaviorSubject<Stage[]>([]);
    this.initializeStages();
  }

  private readonly installStagesSubject: BehaviorSubject<Stage[]>;
  private iceNetInstallStagesSet = new Set();
  private completeInstallStagesSet = new Set();
  private upgradeStagesSet = new Set();

  public getInstallStagesSubject(): BehaviorSubject<Stage[]> {
    return this.installStagesSubject;
  }

  public async initializeStages() {
    let config = await this.installerService.getConfig();
    if (config.runningMode === 'UPGRADE') {
      let upgradeStages = this._stages.filter((stage) => {
        return this.upgradeStagesSet.has(stage.path)
      });
      this.installStagesSubject.next(upgradeStages);
    } else if (config.type === 'ICENET') {
      let iceNetStages = this._stages.filter((stage) => {
        return this.iceNetInstallStagesSet.has(stage.path);
      });
      this.installStagesSubject.next(iceNetStages);
    } else {
      let completeInstallStages = this._stages.filter((stage) => {
        return this.completeInstallStagesSet.has(stage.path);
      });
      this.installStagesSubject.next(completeInstallStages);
    }
  }

  private _stages = [
    {path: 'eula', name: "End User License Agreement"},
    {path: 'installation_type', name: "Installation Type"},
    {path: 'license', name: "Control Center License"},
    {path: 'installation_directory', name: "Installation Directory"},
    {path: 'management_server_configuration', name: "Management Server Connection Details"},
    {path: 'ssl_certificates', name: "SSL Certificates"},
    {path: 'database_configuration', name: "Database Configuration"},
    {path: 'superuser_credentials', name: "Superuser Credentials"},
    {path: 'port_configuration', name: "Port Configuration"},
    {path: 'key_trust_store_password', name: "Keystore & Truststore Password"},
    {path: 'summary', name: "Preinstallation Summary"},
    {path: 'installation', name: "Installation"}]
}
