import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormGroup, Validators, FormControl} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";

@Component({
  selector: 'app-license',
  templateUrl: './license.component.html',
  styleUrls: ['./license.component.scss']
})
export class LicenseComponent implements OnInit, AfterViewInit {

  licenseForm: FormGroup;

  constructor(private installerService: InstallerService, private routerService: RouterService) {
  }

  ngOnInit() {
    this.licenseForm = new FormGroup({
      'license': new FormControl('', Validators.required),
    });
  }

  async onDelete() {
    await this.installerService.deleteLicense().toPromise();
  }

  async formSubmit(){
    this.licenseForm.markAllAsTouched();
    if (this.licenseForm.valid) {
      try {
        // if using cached license, don't upload again
        let license = this.licenseForm.value.license[0];
        license.size && await this.installerService.uploadLicense(license).toPromise();
        this.installerService.setLicenseFile(license);
        this.routerService.navigateNext();
      } catch (e) {
        this.licenseForm.get("license").setErrors({"invalidLicense": true})
      }
    }
  }

  async ngAfterViewInit() {
    let licenseFile = (await this.installerService.getConfig()).licenseFile;
    licenseFile && this.licenseForm.get('license').setValue([licenseFile]);
  }
}
