import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {MatSidenavModule} from '@angular/material/sidenav';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MatListModule} from '@angular/material/list';
import {EulaComponent} from './eula/eula.component';
import {MatButtonModule} from "@angular/material/button";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule, MAT_RADIO_DEFAULT_OPTIONS} from '@angular/material/radio';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {AccessKeyComponent} from './access-key/access-key.component';
import {LayoutComponent} from './layout/layout.component';
import {InstallationTypeComponent} from './installation-type/installation-type.component';
import {LicenseComponent} from './license/license.component';
import {DatabaseConfigComponent} from './database-config/database-config.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SuperUserCredentialsComponent} from './super-user-credentials/super-user-credentials.component';
import {PortConfigurationComponent} from './port-configuration/port-configuration.component';
import {KeystoreTruststorePasswordComponent} from './keystore-truststore-password/keystore-truststore-password.component';
import {PreInstallSummaryComponent} from './pre-install-summary/pre-install-summary.component';
import {InstallationProgressComponent} from './installation-progress/installation-progress.component';
import {CanActivateExitPage, CanActivateStage, CanActivateSuccessPage} from "./installer-can-activate";
import {ActiveStageGuard} from "./active-stage-guard";
import {BottomNavComponent} from './bottom-nav/bottom-nav.component';
import {FileDragDropDirective} from './file-drag-drop.directive';
import {FileDragDropComponent} from './file-drag-drop/file-drag-drop.component';
import {RouterService} from "./router.service";
import {PasswordComponent} from './password/password.component';
import {InstallationSuccessComponent} from './installation-success/installation-success.component';
import {WelcomeLayoutComponent} from './welcome-layout/welcome-layout.component';
import {InstallerHttpInterceptor} from "./installer-http-interceptor";
import { InstallationDirectoryComponent } from './installation-directory/installation-directory.component';
import { ManagementServerConfigComponent } from './management-server-config/management-server-config.component';
import { SslCertUploadComponent } from './ssl-cert-upload/ssl-cert-upload.component';
import { PopupDialogComponent } from './popup-dialog/popup-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import { PostInstallMsgDialogComponent } from './post-install-msg-dialog/post-install-msg-dialog.component';
import { OsAuthConfigMsgComponent } from './os-auth-config-msg/os-auth-config-msg.component';
import { IcenetConfigMsgComponent } from './icenet-config-msg/icenet-config-msg.component';
import {MatStepperModule} from "@angular/material/stepper";
import { InstallationExitComponent } from './installation-exit/installation-exit.component';

@NgModule({
  declarations: [
    AppComponent,
    EulaComponent,
    AccessKeyComponent,
    LayoutComponent,
    InstallationTypeComponent,
    LicenseComponent,
    DatabaseConfigComponent,
    SuperUserCredentialsComponent,
    PortConfigurationComponent,
    KeystoreTruststorePasswordComponent,
    PreInstallSummaryComponent,
    InstallationProgressComponent,
    BottomNavComponent,
    FileDragDropDirective,
    FileDragDropComponent,
    PasswordComponent,
    InstallationSuccessComponent,
    WelcomeLayoutComponent,
    InstallationDirectoryComponent,
    ManagementServerConfigComponent,
    SslCertUploadComponent,
    PopupDialogComponent,
    PostInstallMsgDialogComponent,
    OsAuthConfigMsgComponent,
    IcenetConfigMsgComponent,
    InstallationExitComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCheckboxModule,
    MatRadioModule,
    MatSlideToggleModule,
    MatDialogModule,
    NgbModule,
    MatStepperModule
  ],
  entryComponents: [
    PopupDialogComponent,
    PostInstallMsgDialogComponent,
    OsAuthConfigMsgComponent,
    IcenetConfigMsgComponent,
  ],
  exports: [
    MatSidenavModule
  ],
  providers: [{
    provide: MAT_RADIO_DEFAULT_OPTIONS,
    useValue: {color: 'primary'},
  },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: InstallerHttpInterceptor,
      multi: true
    },
    ActiveStageGuard,
    CanActivateStage,
    CanActivateSuccessPage,
    CanActivateExitPage,
    RouterService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
