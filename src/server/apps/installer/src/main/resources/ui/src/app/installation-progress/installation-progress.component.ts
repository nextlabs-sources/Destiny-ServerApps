import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild
} from '@angular/core';
import {WebSocketService} from "../web-socket.service";
import {ProgressMessage, Task} from "../../models/progress-message";
import {InstallerService} from "../installer.service";
import {Router} from "@angular/router";
import {CanActivateSuccessPage} from "../installer-can-activate";
import {ActiveStageGuard} from "../active-stage-guard";
import {MatDialog} from "@angular/material/dialog";
import {PostInstallMsgDialogComponent} from "../post-install-msg-dialog/post-install-msg-dialog.component";

@Component({
  selector: 'app-installation-progress',
  templateUrl: './installation-progress.component.html',
  styleUrls: ['./installation-progress.component.scss']
})
export class InstallationProgressComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('progressBar', {static: false})
  progressBar: ElementRef;

  @ViewChild('progressPercentage', {static: false})
  progressPercentage: ElementRef;

  icenetInstall: boolean;
  isOSAuthentication: boolean;
  trackTaskGroups: any[];
  taskInProgress: Task;
  installError = false;
  installInProgress = true;

  // Alert user if leaving/ reload page while install in progress
  @HostListener("window:beforeunload", ["$event"]) unloadHandler(event: Event) {
    if (this.installInProgress){
      // Regardless, most modern browsers don't show the custom message
      let msg = "Please wait while the Control Centre server is starting";
      event.returnValue = true;
      return msg;
    }
  }

  constructor(private renderer: Renderer2, private installerService: InstallerService, private webSocketService: WebSocketService,
              private router: Router, private canActivateSuccessPage: CanActivateSuccessPage,
              private dialog: MatDialog) {
  }

  async ngOnInit() {
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  private populateTaskStatus(){
    let i = 0;
    let currentGroup = (this.taskInProgress && this.taskInProgress.group) ||  this.trackTaskGroups[0].name;
    while (i < this.trackTaskGroups.length && this.trackTaskGroups[i].name !== currentGroup){
      this.trackTaskGroups[i].status = "completed";
      i++;
    }
    if (this.installError){
      this.installInProgress = false;
      this.trackTaskGroups[i].status = "error";
      this.renderer.addClass(this.progressBar.nativeElement, "error");
      this.renderer.addClass(this.progressPercentage.nativeElement, "error");
    } else {
      this.trackTaskGroups[i].status = "completed";
    }
  }

  private updateProgressBar(){
    if (this.taskInProgress && this.taskInProgress.progress) {
      this.renderer.setProperty(this.progressBar.nativeElement, "aria-valuenow", this.taskInProgress.progress);
    }
  }

  private updateProgress(){
    this.updateProgressBar();
    this.populateTaskStatus();
    if (this.taskInProgress && this.taskInProgress.progress === 100){
      this.installInProgress = false;
      this.openPostInstallDialog();
    }
  }

  isInstallCompleted(){
    return this.taskInProgress && this.taskInProgress.progress === 100;
  }

  isInstallError(){
    return this.installError;
  }

  async success(){
    if (this.isInstallCompleted()) {
      this.installInProgress = false;
      this.canActivateSuccessPage.installCompleted = true;
      this.router.navigateByUrl('success');
    }
  }

  async ngAfterViewInit() {
    try {
      let config = await this.installerService.getConfig();
      this.icenetInstall = config.type === "ICENET";
      this.isOSAuthentication = config.db.osAuthentication;
      // listen on websocket for progress updates
      this.webSocketService.connect((msg) => {
        let progressMessage = JSON.parse(msg.body);
        this.handleProgress(progressMessage);
      });
      let taskGroups = await this.installerService.getTasks().toPromise();
      this.trackTaskGroups = [];
      taskGroups.map((group, index)=>{
        this.trackTaskGroups[index] = {"name": group, "status": "incomplete"}
      });
      let progressMessage = await this.installerService.getCurrentTask().toPromise();
      this.handleProgress(progressMessage);
    } catch (error) {
      console.log("error getting tasks", error);
    }
  }

  private handleProgress(progressMessage: ProgressMessage){
    this.taskInProgress = progressMessage && progressMessage.task;
    this.installError = progressMessage.installError;
    this.updateProgress();
  }

  openPostInstallDialog(): void {
    if (this.isOSAuthentication || this.icenetInstall){
      const dialogRef = this.dialog.open(PostInstallMsgDialogComponent, {
        width: '800px',
        height: '538px',
        autoFocus: false,
        disableClose: true
      });
    }
  }

}
