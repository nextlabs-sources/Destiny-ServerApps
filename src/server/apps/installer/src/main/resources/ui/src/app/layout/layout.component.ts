import {AfterViewInit, Component} from '@angular/core';
import {InstallerStages, Stage} from "../installer-stages";
import {ActiveStageGuard} from "../active-stage-guard";
import {InstallerService} from "../installer.service";

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements AfterViewInit {

  version: string;
  installMode: string;
  routes: Stage[];

  constructor(private activeStageGuard: ActiveStageGuard, private installerStages: InstallerStages, private installerService: InstallerService) {
    this.installerStages.getInstallStagesSubject().subscribe((stages) => {
      this.routes = stages;
    });
  }

  isStageCompleted(stage: string): boolean {
    return this.activeStageGuard.isCompleted(stage);
  }

  isStageInProgress(stage: string): boolean {
    return this.activeStageGuard.lastInProgressStage === stage;
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    this.version = config.version.ccVersion;
    this.installMode = config.runningMode === 'INSTALLATION'? 'Installation': 'Upgrade';
  }
}
