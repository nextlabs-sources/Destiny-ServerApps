import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {RouterService} from "../router.service";
import {InstallerService} from "../installer.service";

@Component({
  selector: 'app-management-server-config',
  templateUrl: './management-server-config.component.html',
  styleUrls: ['./management-server-config.component.scss']
})
export class ManagementServerConfigComponent implements OnInit, AfterViewInit {

  managementServerConfigForm: FormGroup;
  constructor(private routerService: RouterService, private installerService: InstallerService) { }

  ngOnInit() {
    this.managementServerConfigForm = new FormGroup({
      'configServicePort': new FormControl('', Validators.required),
      'mgmtServicePort': new FormControl('', Validators.required),
      'mgmtServerHost': new FormControl('', Validators.required),
    });
  }

  async formSubmit(){
    if (this.managementServerConfigForm.valid){
      this.installerService.setManagementServerConfig(this.managementServerConfigForm.value.mgmtServerHost,
        this.managementServerConfigForm.value.mgmtServicePort,
        this.managementServerConfigForm.value.configServicePort);
      await this.installerService.uploadConfig().toPromise();
      this.routerService.navigateNext();
    }
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    this.managementServerConfigForm.get('mgmtServerHost').setValue(config.managementServer.host);
    this.managementServerConfigForm.get('mgmtServicePort').setValue(config.managementServer.webServicePort);
    this.managementServerConfigForm.get('configServicePort').setValue(config.managementServer.configServicePort);
  }

}
