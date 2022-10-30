import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, Validators, FormControl, NgForm} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";

@Component({
  selector: 'app-super-user-credentials',
  templateUrl: './super-user-credentials.component.html',
  styleUrls: ['./super-user-credentials.component.scss']
})
export class SuperUserCredentialsComponent implements OnInit, AfterViewInit {

  superUserCredForm: FormGroup;

  constructor(private installerService: InstallerService, private routerService: RouterService) {
  }

  ngOnInit() {
    this.superUserCredForm = new FormGroup({
      'username': new FormControl({value: 'Administrator', disabled: true}),
      'password': new FormControl(''),
    });
  }

  async formSubmit(){
    this.superUserCredForm.get('password').updateValueAndValidity();
    this.superUserCredForm.markAllAsTouched();
    if(this.superUserCredForm.valid){
      try {
        this.installerService.setSuperUserCredentials(this.superUserCredForm.value.password);
        await this.installerService.uploadConfig().toPromise();
        this.routerService.navigateNext();
      } catch (e) {
        console.log("error setting super user credentials");
      }
    }
  }

  async ngAfterViewInit() {
    let config = await this.installerService.getConfig();
    if (config.adminPassword) {
      this.superUserCredForm.get('password').setValue(config.adminPassword);
    }
  }

}
