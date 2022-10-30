import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {ActiveStageGuard} from "./active-stage-guard";
import {Observable} from "rxjs";
import {InstallerStages, Stage} from "./installer-stages";

@Injectable()
export class CanActivateStage implements CanActivate {

  private routes: Stage[];

  constructor(private activeStageGuard: ActiveStageGuard, private router: Router, private installerStages: InstallerStages) {
    this.installerStages.getInstallStagesSubject().subscribe((stages) => {
      this.routes = stages;
    });
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean|UrlTree>|Promise<boolean|UrlTree>|boolean|UrlTree {
    // In case of browser refresh, state is not present in memory
    if (this.activeStageGuard.isStateEmpty()){
      this.activeStageGuard.loadState();
    }
    if(this.activeStageGuard.canActivate(route.url[0].path)) {
      return true;
    } else if(this.isValidRoute(route.url[0].path) && this.activeStageGuard.lastInProgressStage){
      this.router.navigate([`installer/${this.activeStageGuard.lastInProgressStage}`]);
    } else {
      this.router.navigate([``]);
    }
    return false;
  }

  private isValidRoute(stage: string): boolean {
    let filtered = this.routes.filter((route) => {
      return route.path == stage;
    });
    return filtered.length > 0;
  }
}

@Injectable()
export class CanActivateSuccessPage implements CanActivate {

  private _installCompleted = false;

  constructor() {}

  set installCompleted(value: boolean) {
    this._installCompleted = value;
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean|UrlTree>|Promise<boolean|UrlTree>|boolean|UrlTree {
    return this._installCompleted;
  }
}

@Injectable()
export class CanActivateExitPage implements CanActivate {

  private _installExited = false;

  constructor() {}

  set installExited(value: boolean) {
    this._installExited = value;
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean|UrlTree>|Promise<boolean|UrlTree>|boolean|UrlTree {
    return this._installExited;
  }
}
