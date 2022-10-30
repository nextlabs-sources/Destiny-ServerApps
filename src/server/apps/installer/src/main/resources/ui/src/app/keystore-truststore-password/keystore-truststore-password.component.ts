import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, Validators, FormControl, NgForm} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";
import {PASSWORD_FIELD_TOUCHED} from "../installer-constants";

@Component({
  selector: 'app-keystore-truststore-password',
  templateUrl: './keystore-truststore-password.component.html',
  styleUrls: ['./keystore-truststore-password.component.scss']
})
export class KeystoreTruststorePasswordComponent implements OnInit, AfterViewInit {

  keyStoreTrustStoreForm: FormGroup;

  constructor(private installerService: InstallerService, private routerService: RouterService) {
  }

  ngOnInit() {
    this.keyStoreTrustStoreForm = new FormGroup({
      'keyStorePassword': new FormControl('', Validators.required),
      'trustStorePassword': new FormControl('', Validators.required)
    });
  }

  async formSubmit() {
    if (this.keyStoreTrustStoreForm.valid) {
      this.installerService.setKeyStoreTrustStorePassword(this.keyStoreTrustStoreForm.value.keyStorePassword, this.keyStoreTrustStoreForm.value.trustStorePassword);
      this.installerService.uploadConfig().toPromise();
      this.routerService.navigateNext();
    } else {
      this.setFieldAsTouched();
    }
  }

  setFieldAsTouched(){
    // Angular as of now doesn't support mark as touched from parent
    let keyStorePass = this.keyStoreTrustStoreForm.value.keyStorePassword;
    let trustStorePass = this.keyStoreTrustStoreForm.value.trustStorePassword;
    this.keyStoreTrustStoreForm.get('keyStorePassword').setValue(PASSWORD_FIELD_TOUCHED);
    this.keyStoreTrustStoreForm.get('trustStorePassword').setValue(PASSWORD_FIELD_TOUCHED);
    this.keyStoreTrustStoreForm.get('keyStorePassword').setValue(keyStorePass);
    this.keyStoreTrustStoreForm.get('trustStorePassword').setValue(trustStorePass);
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    if (config.ssl.keystore.password && config.ssl.truststore.password) {
      this.keyStoreTrustStoreForm.get('keyStorePassword').setValue(config.ssl.keystore.password);
      this.keyStoreTrustStoreForm.get('trustStorePassword').setValue(config.ssl.truststore.password);
      if (this.keyStoreTrustStoreForm.get('keyStorePassword').errors || this.keyStoreTrustStoreForm.get('trustStorePassword').errors) {
        this.keyStoreTrustStoreForm.get('keyStorePassword').setValue('');
        this.keyStoreTrustStoreForm.get('trustStorePassword').setValue('');
      }
    }
  }
}
