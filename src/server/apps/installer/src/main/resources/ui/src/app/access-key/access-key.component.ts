import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators, FormControl} from '@angular/forms';
import {Router} from '@angular/router';
import {InstallerService} from "../installer.service";
import {ActiveStageGuard} from "../active-stage-guard";

@Component({
  selector: 'app-welcome',
  templateUrl: './access-key.component.html',
  styleUrls: ['./access-key.component.scss']
})
export class AccessKeyComponent implements OnInit{

  welcomeForm: FormGroup;
  showPassword = false;

  constructor(private router : Router, private installerService: InstallerService, private activeStageGuard: ActiveStageGuard){
  }

  ngOnInit(): void {
    this.activeStageGuard.clearState();
    this.installerService.setConfig(null);
    this.welcomeForm = new FormGroup({
      'accessKey': new FormControl(null, Validators.required),
    });
  }

  togglePasswordHide() {
    this.showPassword = !this.showPassword;
  }

  public async startInstaller() {
    if(this.welcomeForm.invalid){
      return;
    }
    try {
      this.installerService.setKey(this.welcomeForm.value.accessKey.trim());
      // do validation on key by trying to retrieve config from server
      await this.installerService.getConfig();
      this.activeStageGuard.addInProgressStage('eula');
      this.router.navigate(['/installer/eula']);
    } catch (e) {
      this.welcomeForm.get('accessKey').setErrors({"keyError":true})
    }
  }
}
