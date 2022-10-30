import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";

@Component({
  selector: 'app-ssl-cert-upload',
  templateUrl: './ssl-cert-upload.component.html',
  styleUrls: ['./ssl-cert-upload.component.scss']
})
export class SslCertUploadComponent implements OnInit, AfterViewInit {

  sslCertForm: FormGroup;

  constructor(private installerService: InstallerService, private routerService: RouterService) { }

  ngOnInit() {
    this.sslCertForm = new FormGroup({
      'certificate': new FormControl([]),
    });
  }

  async formSubmit(){
    this.sslCertForm.markAllAsTouched();
    if (this.sslCertForm.valid) {
      try {
        if (this.sslCertForm.value.certificate) {
          for (let serverCert of this.sslCertForm.value.certificate) {
            // skip uploading pre-existing certificates
            serverCert.size && await this.installerService.uploadCACertificate(serverCert).toPromise();
          }
          this.installerService.setSslCertFiles(this.sslCertForm.value.certificate);
        }
        this.routerService.navigateNext();
      } catch (e) {
        this.sslCertForm.get("certificate").setErrors({"invalidCertificate": true})
      }
    }
  }

  async ngAfterViewInit() {
    let uploadedSslCerts = (await this.installerService.getConfig()).sslCertFiles;
    uploadedSslCerts && this.sslCertForm.get('certificate').setValue(uploadedSslCerts);
  }

  async deleteSslCert(certFile: File){
    await this.installerService.deleteCACertificate(certFile.name).toPromise();
  }

}
