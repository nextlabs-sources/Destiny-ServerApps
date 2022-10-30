import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, Validators, FormControl, NgForm} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";

@Component({
  selector: 'app-port-configuration',
  templateUrl: './port-configuration.component.html',
  styleUrls: ['./port-configuration.component.scss']
})
export class PortConfigurationComponent implements OnInit, AfterViewInit {

  portConfigForm: FormGroup;

  iceNetInstall: boolean;

  errors: any;

  constructor(private installerService: InstallerService, private routerService: RouterService) {
  }

  ngOnInit() {
    this.portConfigForm = new FormGroup({
      'webServicePort': new FormControl('', [Validators.required,  Validators.min(1), Validators.max(65535)]),
      'appServicePort': new FormControl('', [Validators.required,  Validators.min(1), Validators.max(65535)]),
      'policyValidatorPort': new FormControl('', [Validators.required,  Validators.min(1), Validators.max(65535)]),
      'configServicePort': new FormControl('', [Validators.required,  Validators.min(1), Validators.max(65535)]),
      'activeMqPort': new FormControl('', [Validators.required,  Validators.min(1), Validators.max(65535)]),
    });
  }

  async formSubmit() {
    this.portConfigForm.markAllAsTouched();
    if (this.portConfigForm.valid) {
      this.installerService.setPorts(this.portConfigForm.value.appServicePort, this.portConfigForm.value.policyValidatorPort, this.portConfigForm.value.configServicePort,
        this.portConfigForm.value.webServicePort, this.portConfigForm.value.activeMqPort);
      try {
        await this.installerService.validatePorts().toPromise();
        await this.installerService.uploadConfig().toPromise();
        this.routerService.navigateNext();
      } catch (exception) {
        console.log("failed uploading ports", exception);
        if (exception.error && exception.error.errors) {
          this.errors = exception.error.errors;
          Object.keys(exception.error.errors).forEach((errorFormControl) => {
            this.portConfigForm.get(errorFormControl).setErrors({"portError": true});
          });
        }
      }
    }
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    this.portConfigForm.get('webServicePort').setValue(config.port.webServicePort);
    this.portConfigForm.get('appServicePort').setValue(config.port.appServicePort);
    this.portConfigForm.get('policyValidatorPort').setValue(config.port.policyValidatorPort);
    this.portConfigForm.get('configServicePort').setValue(config.port.configServicePort);
    this.portConfigForm.get('activeMqPort').setValue(config.port.activeMqPort);
    if (config.type === 'ICENET'){
      this.iceNetInstall = true;
      this.portConfigForm.get('appServicePort').setValidators([]);
      this.portConfigForm.get('policyValidatorPort').setValidators([]);
      this.portConfigForm.get('configServicePort').setValidators([]);
      this.portConfigForm.get('activeMqPort').setValidators([]);
      this.portConfigForm.get('appServicePort').updateValueAndValidity();
      this.portConfigForm.get('policyValidatorPort').updateValueAndValidity();
      this.portConfigForm.get('configServicePort').updateValueAndValidity();
      this.portConfigForm.get('activeMqPort').updateValueAndValidity();
    }
  }
}
