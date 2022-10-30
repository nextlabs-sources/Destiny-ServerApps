import { NgModule } from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import { LayoutComponent } from "./layout/layout.component";
import { EulaComponent } from "./eula/eula.component";
import { AccessKeyComponent } from "./access-key/access-key.component";
import { InstallationTypeComponent } from "./installation-type/installation-type.component";
import { LicenseComponent} from "./license/license.component";
import { DatabaseConfigComponent } from "./database-config/database-config.component";
import { SuperUserCredentialsComponent } from "./super-user-credentials/super-user-credentials.component";
import { PortConfigurationComponent } from "./port-configuration/port-configuration.component";
import { KeystoreTruststorePasswordComponent } from "./keystore-truststore-password/keystore-truststore-password.component";
import { PreInstallSummaryComponent } from "./pre-install-summary/pre-install-summary.component";
import { InstallationProgressComponent } from "./installation-progress/installation-progress.component";
import {WelcomeLayoutComponent} from "./welcome-layout/welcome-layout.component";
import {InstallationSuccessComponent} from "./installation-success/installation-success.component";
import {CanActivateExitPage, CanActivateStage, CanActivateSuccessPage} from "./installer-can-activate";
import {PATH_PREFIX} from "./installer-constants";
import {InstallationDirectoryComponent} from "./installation-directory/installation-directory.component";
import {ManagementServerConfigComponent} from "./management-server-config/management-server-config.component";
import {SslCertUploadComponent} from "./ssl-cert-upload/ssl-cert-upload.component";
import {InstallationExitComponent} from "./installation-exit/installation-exit.component";

export const routes: Routes = [
  {
    path: PATH_PREFIX, component: LayoutComponent, data: {name: "Installer Layout"},
    children: [
      {path: 'eula', component: EulaComponent, canActivate: [CanActivateStage]},
      {path: 'installation_type', component: InstallationTypeComponent, canActivate: [CanActivateStage]},
      {path: 'license', component: LicenseComponent, canActivate: [CanActivateStage]},
      {path: 'installation_directory', component: InstallationDirectoryComponent, canActivate: [CanActivateStage]},
      {path: 'management_server_configuration', component: ManagementServerConfigComponent, canActivate: [CanActivateStage]},
      {path: 'ssl_certificates', component: SslCertUploadComponent, canActivate: [CanActivateStage]},
      {path: 'database_configuration', component: DatabaseConfigComponent, canActivate: [CanActivateStage]},
      {path: 'superuser_credentials', component: SuperUserCredentialsComponent, canActivate: [CanActivateStage]},
      {path: 'port_configuration', component: PortConfigurationComponent, canActivate: [CanActivateStage]},
      {path: 'key_trust_store_password', component: KeystoreTruststorePasswordComponent, canActivate: [CanActivateStage]},
      {path: 'summary', component: PreInstallSummaryComponent, canActivate: [CanActivateStage]},
      {path: 'installation', component: InstallationProgressComponent, canActivate: [CanActivateStage]}
    ]
  },
  {
    path: '', component: WelcomeLayoutComponent, data: {name: "Welcome Page"},
    children: [
      {path: '', component: AccessKeyComponent},
      {path: 'success', component: InstallationSuccessComponent, canActivate: [CanActivateSuccessPage]},
      {path: 'exit', component: InstallationExitComponent, canActivate: [CanActivateExitPage]}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
  public appRoutes = routes;
}
