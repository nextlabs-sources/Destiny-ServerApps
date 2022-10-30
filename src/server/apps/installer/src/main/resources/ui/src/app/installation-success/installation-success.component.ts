import {
  AfterViewInit,
  Component,
  HostListener,
  Renderer2,
} from '@angular/core';
import {InstallerService} from "../installer.service";
import {ActiveStageGuard} from "../active-stage-guard";

@Component({
  selector: 'app-success',
  templateUrl: './installation-success.component.html',
  styleUrls: ['./installation-success.component.scss']
})
export class InstallationSuccessComponent implements AfterViewInit {

  serverStarted = false;
  installType: string;

  // Alert user if leaving/ reload page before CC server starts
  @HostListener("window:beforeunload", ["$event"]) unloadHandler(event: Event) {
    if (!this.serverStarted) {
      // Regardless, most modern browsers don't show the custom message
      let msg = "Please wait while the Control Centre server is starting";
      event.returnValue = true;
      return msg;
    }
  }

  constructor(private installerService: InstallerService, private activeStageGuard: ActiveStageGuard) {
  }

  async ngAfterViewInit() {

    this.installType = await this.installerService.getType();
    this.activeStageGuard.clearState();
    this.installerService.setConfig(null);

    try {
      await this.installerService.createMarker().toPromise();
      await this.installerService.exit().toPromise();
    } catch (e) {
      // installer restarting
    }
    // since there is no simple way to detect server startup completion,
    // show navigate to server after 5 min
    setTimeout(() => {
      this.serverStarted = true;
    }, 1000 * 60 * 5);
  }

  isIcenetInstall(): boolean {
    return this.installType === 'ICENET';
  }
}
