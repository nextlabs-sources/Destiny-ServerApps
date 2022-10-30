import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";
import {Config} from "../../models/config";

@Component({
  selector: 'app-installation-directory',
  templateUrl: './installation-directory.component.html',
  styleUrls: ['./installation-directory.component.scss']
})
export class InstallationDirectoryComponent implements OnInit, AfterViewInit {

  installationDirectoryForm: FormGroup;
  config: Config;

  constructor(private routerService: RouterService, private installerService: InstallerService) { }

  ngOnInit() {
    this.installationDirectoryForm = new FormGroup({
      'installationDirectoryConfig': new FormControl('default'),
      'defaultInstallDirectory': new FormControl({value: '', disabled: true}),
      'customInstallDirectory': new FormControl({value: '', disabled: true})
    });
  }

  async ngAfterViewInit() {
    this.installationDirectoryForm.get('installationDirectoryConfig').valueChanges
      .subscribe(directoryMode => {
      if (directoryMode === 'custom'){
        this.installationDirectoryForm.get('customInstallDirectory').enable();
        this.installationDirectoryForm.get('customInstallDirectory').setValidators([Validators.required]);
      } else {
        this.installationDirectoryForm.get('customInstallDirectory').disable();
        this.installationDirectoryForm.get('customInstallDirectory').setValidators([]);
      }
      this.installationDirectoryForm.get('customInstallDirectory').updateValueAndValidity();
    });
    this.config = await this.installerService.getConfig();
    if (this.isUpgrade()) {
      this.installationDirectoryForm.get('defaultInstallDirectory').setValue(this.config.previousHome);
      this.installationDirectoryForm.get('customInstallDirectory').setValue(this.config.home);
    } else {
      this.installationDirectoryForm.get('installationDirectoryConfig').setValue('custom');
      this.installationDirectoryForm.get('customInstallDirectory').setValue(this.config.home);
    }
    if (this.config.installationPath){
      this.installationDirectoryForm.get('installationDirectoryConfig').setValue('custom');
      this.installationDirectoryForm.get('customInstallDirectory').setValue(this.config.installationPath);
    }
  }

  async formSubmit() {
    if (this.installationDirectoryForm.valid) {
      try {
        if (this.installationDirectoryForm.value.installationDirectoryConfig === 'custom' &&
          (this.installPathUpdated() || this.isUpgrade())) {
          this.installerService.setUpgradeExisting(false);
          this.installerService.setInstallationPath(this.installationDirectoryForm.value.customInstallDirectory);
          await this.installerService
            .validateInstallationPath(this.installationDirectoryForm.value.customInstallDirectory)
            .toPromise();
        } else {
          this.installerService.setUpgradeExisting(true);
          this.installerService.setInstallationPath('');
        }
        await this.installerService.uploadConfig().toPromise();
        this.routerService.navigateNext();
      } catch (exception) {
        this.installationDirectoryForm.get('customInstallDirectory').setErrors({"pathError": true});
      }
    }
  }

  isUpgrade() {
    return this.config && this.config.runningMode === 'UPGRADE';
  }

  installPathUpdated(): boolean{
    return this.config && this.config.home !== this.installationDirectoryForm.value.customInstallDirectory;
  }

  resetInstallPath() {
    this.installationDirectoryForm.get('customInstallDirectory').setValue(this.config.home);
  }

}
