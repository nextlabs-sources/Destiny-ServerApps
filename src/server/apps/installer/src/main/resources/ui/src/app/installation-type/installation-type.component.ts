import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, FormGroupDirective, FormControl} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";
import {InstallerStages} from "../installer-stages";
import {ActiveStageGuard} from "../active-stage-guard";

@Component({
  selector: 'app-installation-type',
  templateUrl: './installation-type.component.html',
  styleUrls: ['./installation-type.component.scss']
})
export class InstallationTypeComponent implements OnInit, AfterViewInit {

  @ViewChild('formRef', {static: false})
  formRef: FormGroupDirective;

  installationTypeForm: FormGroup;

  constructor(private installerService: InstallerService, private routerService: RouterService,
              private installerStages: InstallerStages, private activeStageGuard: ActiveStageGuard) {
  }

  async ngOnInit() {
    this.installationTypeForm = new FormGroup({
      'installationType': new FormControl('')
    });
  }

  async formSubmit() {
    if(this.installationTypeForm.valid) {
      let previousType = (await this.installerService.getConfig()).type;
      this.installerService.setInstallationType(this.installationTypeForm.value.installationType);
      this.installerService.uploadConfig().toPromise();
      // if user updated install type
      if (previousType === 'ICENET' || this.installationTypeForm.value.installationType === 'ICENET'){
        this.resetState();
        this.installerStages.initializeStages();
      }
      // wait for the route list changes to take effect
      setTimeout(() => {
        this.routerService.navigateNext();
      }, 50);
    }
  }

  private resetState(){
    this.activeStageGuard.clearState();
    this.activeStageGuard.addCompleted('eula');
    this.activeStageGuard.addCompleted('installation_type');
    this.activeStageGuard.addInProgressStage('eula');
    this.activeStageGuard.addInProgressStage('installation_type');
    if (this.installationTypeForm.value.installationType === 'ICENET'){
      this.activeStageGuard.addInProgressStage('installation_directory');
    } else {
      this.activeStageGuard.addInProgressStage('license');
    }
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    this.installationTypeForm.get('installationType').setValue(config.type);
  }
}
