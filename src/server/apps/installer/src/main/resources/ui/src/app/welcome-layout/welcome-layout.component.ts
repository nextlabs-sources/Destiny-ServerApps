import {Component, Input, OnInit} from '@angular/core';
import {InstallerService} from "../installer.service";

@Component({
  selector: 'app-welcome-layout',
  templateUrl: './welcome-layout.component.html',
  styleUrls: ['./welcome-layout.component.scss']
})
export class WelcomeLayoutComponent implements OnInit {

  @Input() showSubText: boolean;

  constructor(private installerService: InstallerService) { }

  ngOnInit() {
    // Show installer sub text only on welcome page and not on success page
    this.showSubText = !this.installerService.installStarted;
  }

}
