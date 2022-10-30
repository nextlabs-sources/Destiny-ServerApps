import {Component} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {RouterService} from "../router.service";

@Component({
  selector: 'app-eula',
  templateUrl: './eula.component.html',
  styleUrls: ['./eula.component.scss']
})
export class EulaComponent {

  constructor(private formBuilder: FormBuilder, private routerService: RouterService) {
  }

  formSubmit(){
    this.routerService.navigateNext();
  }
}
