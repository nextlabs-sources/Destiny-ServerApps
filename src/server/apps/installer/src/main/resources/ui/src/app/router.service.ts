import {Injectable} from '@angular/core';
import {PATH_PREFIX} from "./installer-constants";
import {ActiveStageGuard} from "./active-stage-guard";
import {Router, ActivatedRoute} from '@angular/router';
import {InstallerStages, Stage} from "./installer-stages";

@Injectable()
export class RouterService {

  private routes: Stage[];

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private activeStageGuard: ActiveStageGuard, private installerStages: InstallerStages) {
    this.installerStages.getInstallStagesSubject().subscribe((stages) => {
      this.routes = stages;
    });
  }


  public navigateNext() {
    let currentIndex = this.getCurrentRouteIndex();
    if (currentIndex < this.routes.length - 1) {
      let nextPath = this.routes[currentIndex + 1].path;
      this.activeStageGuard.addCompleted(this.getCurrentRoute());
      this.activeStageGuard.addInProgressStage(nextPath);
      this.router.navigate([`${PATH_PREFIX}/${nextPath}`]);
    }
  }

  public navigateBack() {
    let currentIndex = this.getCurrentRouteIndex();
    if (currentIndex > 0) {
      let backPath = this.routes[currentIndex - 1].path;
      this.router.navigate([`${PATH_PREFIX}/${backPath}`]);
    }
  }

  private getCurrentRouteIndex(): number {
    return this.routes
      .map((route) => {
        return route.path;
      })
      .indexOf(this.getCurrentRoute());
  }

  private getCurrentRoute(): string {
    let routeComponents = this.router.url.split('/');
    return routeComponents[routeComponents.length - 1];
  }
}
