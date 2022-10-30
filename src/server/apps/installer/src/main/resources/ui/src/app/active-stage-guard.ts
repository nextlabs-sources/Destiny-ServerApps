import {Injectable} from "@angular/core";

@Injectable()
export class ActiveStageGuard {
  private completedStages: Set<string> = new Set();
  private inProgressStages: Set<string> = new Set();
  private _lastInProgressStage: string;

  get lastInProgressStage(): string {
    return this._lastInProgressStage;
  }

  set lastInProgressStage(value: string) {
    this._lastInProgressStage = value;
  }

  canActivate(stage: string): boolean {
    if (this.installStarted()){
      return stage === "installation";
    }
    return this.isInProgress(stage) || this.isCompleted(stage);
  }

  addInProgressStage(stage: string) {
    if(!this.inProgressStages.has(stage)) {
      this.inProgressStages.add(stage);
      this._lastInProgressStage = stage;
      this.saveState();
    }
  }

  addCompleted(stage: string) {
    if(!this.completedStages.has(stage)) {
      this.completedStages.add(stage);
      this.saveState();
    }
  }

  isInProgress(stage: string) {
    return this.inProgressStages && this.inProgressStages.has(stage);
  }

  isCompleted(stage: string) {
    return this.completedStages && this.completedStages.has(stage);
  }

  installStarted(): boolean{
    return this.completedStages && this.completedStages.has('summary');
  }

  saveState(){
    sessionStorage.setItem("completedStages", Array.from(this.completedStages).join(','));
    sessionStorage.setItem("inProgressStage", Array.from(this.inProgressStages).join(','));
    sessionStorage.setItem("lastInProgressStage",this.lastInProgressStage);

  }

  loadState(){
    this.lastInProgressStage = sessionStorage.getItem("lastInProgressStage");
    let inProgressStages = sessionStorage.getItem("inProgressStage");
    if (inProgressStages){
      this.inProgressStages = new Set(inProgressStages.split(','));
    }
    let completedStages: string = sessionStorage.getItem("completedStages");
    if (completedStages){
      this.completedStages = new Set(completedStages.split(','));
    }
  }

  clearState(){
    this.lastInProgressStage = '';
    this.inProgressStages = new Set();
    this.completedStages = new Set();
    this.saveState();
  }

  isStateEmpty(){
    return !(this.inProgressStages && this.completedStages && this.lastInProgressStage);
  }
}
