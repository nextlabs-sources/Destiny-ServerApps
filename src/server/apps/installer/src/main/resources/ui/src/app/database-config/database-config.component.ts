import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormGroup, NgForm, Validators, FormControl} from '@angular/forms';
import {InstallerService} from "../installer.service";
import {RouterService} from "../router.service";
import {DBConfig} from "../../models/db-config";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {PopupDialogComponent} from "../popup-dialog/popup-dialog.component";

@Component({
  selector: 'app-database-config',
  templateUrl: './database-config.component.html',
  styleUrls: ['./database-config.component.scss']
})
export class DatabaseConfigComponent implements OnInit, AfterViewInit {

  @ViewChild('ngForm', {static: false})
  formRef: NgForm;
  dbConfigForm: FormGroup;
  BASIC_CONFIG_KEY = "basic";
  ADVANCED_CONFIG_KEY = "advanced";
  serverDomainLabel = "Hostname in Certificate";
  showSchema = false;
  showAuthType = true;
  validationRequestInProgress = false;
  dialogRef: MatDialogRef<PopupDialogComponent>;

  available_dbs = [
    {type: 'DB2', name: 'DB2', defaultPort: 50000, allowWindowsAuth: false, schemaRequired: true},
    {type: 'SQL_SERVER', name: 'MSSQL', defaultPort: 1433, allowWindowsAuth: true, schemaRequired: false},
    {type: 'ORACLE', name: 'ORACLE', defaultPort: 1521, allowWindowsAuth: false, schemaRequired: false},
    {type: 'POSTGRESQL', name: 'POSTGRESQL', defaultPort: 5432, allowWindowsAuth: false, schemaRequired: false}
  ];

  constructor(private installerService: InstallerService, private routerService: RouterService, private dialog: MatDialog) {
  }

  ngOnInit() {
    this.dbConfigForm = new FormGroup({
      'configMode': new FormControl(''),
      'jdbcUrl': new FormControl(''),
      'dbServer': new FormControl(''),
      'hostname': new FormControl(''),
      'port': new FormControl(''),
      'dbSid': new FormControl(''),
      'dbSchema': new FormControl(''),
      'sslConnection': new FormControl(false),
      'validateServer': new FormControl({value: false, disabled: true}),
      'serverDomain': new FormControl({value: '', disabled: true}),
      'authType': new FormControl(''),
      'username': new FormControl(''),
      'password': new FormControl(''),
    });
    this.dbConfigForm.get('sslConnection').valueChanges
      .subscribe(ssl_connection => {
        if (!ssl_connection) {
          this.clearCertificateAndDomain();
          this.dbConfigForm.get('validateServer').setValue(false);
          this.dbConfigForm.get('validateServer').disable();
        } else {
          this.dbConfigForm.get('validateServer').enable();
        }
      });
    this.dbConfigForm.get('dbServer').valueChanges
      .subscribe(dbServer => {
        this.clearFields();
        let db = this.available_dbs.filter((db) => {
          return db.type === dbServer;
        });
        if (db){
          this.dbConfigForm.get('port').setValue(db[0].defaultPort);
          this.showSchema = db[0].schemaRequired;
          if(this.showSchema) {
            this.dbConfigForm.get('dbSchema').enable();
            this.dbConfigForm.get('dbSchema').setValidators([Validators.required]);
            this.dbConfigForm.get('dbSchema').updateValueAndValidity();
          } else {
            this.dbConfigForm.get('dbSchema').disable();
          }
          this.showAuthType = db[0].allowWindowsAuth;
          if (!this.showAuthType){
            this.dbConfigForm.get('authType').setValue('nativeAuth');
          }
        }
        if (dbServer === "DB2" || dbServer === "SQL_SERVER") {
          this.serverDomainLabel = "Hostname in Certificate";
        } else if (dbServer === "ORACLE") {
          this.serverDomainLabel = "Server Certificate DN";
        }
      });
    this.dbConfigForm.get('validateServer').valueChanges
      .subscribe(validate_server => {
        if (!validate_server) {
          this.clearCertificateAndDomain();
        } else if (this.showServerDomain()){
          this.enableCertificateAndDomain();
        }
        this.dbConfigForm.get('serverDomain').updateValueAndValidity();
      });
    this.setupAuthType();
    this.setupConfigMode();
  }

  private setupAuthType() {
    this.dbConfigForm.get('authType').valueChanges
      .subscribe(authType => {
        if (authType === 'osAuth') {
          this.dbConfigForm.get('username').disable();
          this.dbConfigForm.get('password').disable();
          this.dbConfigForm.get('username').clearValidators();
          this.dbConfigForm.get('password').clearValidators();
        } else {
          this.dbConfigForm.get('username').enable();
          this.dbConfigForm.get('username').setValidators([Validators.required]);
          this.dbConfigForm.get('username').updateValueAndValidity();
          this.dbConfigForm.get('username').markAsUntouched();
          this.dbConfigForm.get('username').markAsPristine();
          this.dbConfigForm.get('password').enable();
          this.dbConfigForm.get('password').setValidators([Validators.required]);
          this.dbConfigForm.get('password').updateValueAndValidity();
          this.dbConfigForm.get('password').markAsUntouched();
          this.dbConfigForm.get('password').markAsPristine();
        }
      });
  }

  private setupConfigMode() {
    this.dbConfigForm.get('configMode').valueChanges
      .subscribe(async config_mode => {
        if (config_mode == this.ADVANCED_CONFIG_KEY) {
          if (this.dbConfigForm.value.hostname && this.dbConfigForm.value.port && this.dbConfigForm.value.dbSid) {
            let dbConfig = this.getConfig();
            let res = await this.installerService.getJdbcUrl(dbConfig).toPromise();
            this.dbConfigForm.get('jdbcUrl').setValue(res.jdbcUrl);
            this.dbConfigForm.get('hostname').setValue('');
            this.dbConfigForm.get('port').setValue('');
            this.dbConfigForm.get('dbSid').setValue('');
            this.dbConfigForm.get('dbSchema').setValue('');
          }
          this.dbConfigForm.get('hostname').disable();
          this.dbConfigForm.get('port').disable();
          this.dbConfigForm.get('dbSid').disable();
          this.dbConfigForm.get('dbSchema').disable();
          this.dbConfigForm.get('username').enable();
          this.dbConfigForm.get('password').enable();
          this.dbConfigForm.get('sslConnection').setValue(false);
          this.dbConfigForm.get('validateServer').setValue(false);

          this.dbConfigForm.get('jdbcUrl').setValidators([Validators.required]);
          this.dbConfigForm.get('jdbcUrl').updateValueAndValidity();
        } else {
          this.dbConfigForm.get('hostname').enable();
          this.dbConfigForm.get('hostname').setValidators([Validators.required]);
          this.dbConfigForm.get('hostname').updateValueAndValidity();
          this.dbConfigForm.get('port').enable();
          this.dbConfigForm.get('port').setValidators([Validators.required, Validators.min(1), Validators.max(65535)]);
          this.dbConfigForm.get('port').updateValueAndValidity();
          this.dbConfigForm.get('dbSid').enable();
          this.dbConfigForm.get('dbSid').setValidators([Validators.required]);
          this.dbConfigForm.get('dbSid').updateValueAndValidity();
          if(this.showSchema) {
            this.dbConfigForm.get('dbSchema').enable();
            this.dbConfigForm.get('dbSchema').setValidators([Validators.required]);
            this.dbConfigForm.get('dbSchema').updateValueAndValidity();
          } else {
            this.dbConfigForm.get('dbSchema').disable();
          }
          this.dbConfigForm.get('jdbcUrl').clearValidators();
          this.dbConfigForm.get('jdbcUrl').markAsPristine();
          if (this.dbConfigForm.value.jdbcUrl) {
            let dbConfigParsed = await this.installerService.getDbDetails(this.dbConfigForm.value.jdbcUrl).toPromise();
            this.updateValues(dbConfigParsed, false);
          }
          this.dbConfigForm.get('jdbcUrl').setValue('');
          this.dbConfigForm.get('jdbcUrl').updateValueAndValidity();
        }
      });
  }

  private clearFields() {
    this.dbConfigForm.get('hostname').setValue('');
    this.dbConfigForm.get('port').setValue('');
    this.dbConfigForm.get('dbSid').setValue('');
    this.dbConfigForm.get('dbSchema').setValue('');
    this.dbConfigForm.get('username').setValue('');
    this.dbConfigForm.get('password').setValue('');
    this.dbConfigForm.get('serverDomain').setValue('');
    this.dbConfigForm.get('sslConnection').setValue(false);
    this.dbConfigForm.get('validateServer').setValue(false);
  }

  private getConfig(): DBConfig{
    let dbConfig = new DBConfig();
    dbConfig.url = this.dbConfigForm.value.jdbcUrl;
    dbConfig.username = this.dbConfigForm.value.username;
    dbConfig.password = this.dbConfigForm.value.password;
    dbConfig.configMode = this.dbConfigForm.value.configMode;
    dbConfig.host = this.dbConfigForm.value.hostname;
    dbConfig.port = this.dbConfigForm.value.port;
    dbConfig.databaseName = this.dbConfigForm.value.dbSid;
    if(this.showSchema) {
      dbConfig.schemaName = this.dbConfigForm.value.dbSchema;
    }
    dbConfig.dbType = this.dbConfigForm.value.dbServer;
    dbConfig.ssl = this.dbConfigForm.value.sslConnection;
    dbConfig.validateServerCertificate = this.dbConfigForm.value.validateServer;
    dbConfig.hostNameInCertificate = this.dbConfigForm.value.serverDomain;
    dbConfig.osAuthentication = this.isOSAuthentication();
    return dbConfig;
  }

  async validateDBConnection(showDialogOnSuccess: boolean = true): Promise<boolean> {
    if (this.dbConfigForm.valid) {
      if (this.validationRequestInProgress) {
        this.openDialog("Connection test in progress", "inprogress");
      } else {
        try {
          this.installerService.setDbConfig(this.getConfig());
          this.validationRequestInProgress = true;
          await this.installerService.validateDbConnection().toPromise();
          this.validationRequestInProgress = false;
          if (showDialogOnSuccess) {
            this.openDialog("Connection successful", "success");
          }
          return new Promise((resolve) => {
            resolve(true);
          });
        } catch (exception) {
          this.validationRequestInProgress = false;
          this.dbConfigForm.updateValueAndValidity();
          this.openDialog("Connection failed", "error");
          return new Promise((resolve) => {
            resolve(false);
          });
        }
      }
    }
  }

  async formSubmit() {
    try {
      if (await this.validateDBConnection(false)) {
        await this.installerService.uploadConfig().toPromise();
        this.routerService.navigateNext();
      }
    } catch (exception) {
    }
  }

  isAdvancedConfig(): boolean {
    return this.dbConfigForm.get('configMode').value == this.ADVANCED_CONFIG_KEY;
  }

  isSslConnectionEnabled(): boolean {
    return this.dbConfigForm.get('sslConnection').value;
  }

  isValidateServer(): boolean {
    return this.dbConfigForm.get('validateServer').value || this.dbConfigForm.value.configMode == this.ADVANCED_CONFIG_KEY;
  }

  isOSAuthentication(): boolean {
    return this.dbConfigForm.value.authType === 'osAuth';
  }

  showServerDomain(): boolean {
    return !this.isAdvancedConfig() && this.isSslConnectionEnabled() && this.dbConfigForm.value.dbServer !== 'POSTGRESQL';
  }

  clearCertificateAndDomain() {
    this.dbConfigForm.get('serverDomain').setValue(null);
    this.dbConfigForm.get('serverDomain').disable();
    this.dbConfigForm.get('serverDomain').setValidators([]);
  }

  enableCertificateAndDomain() {
    this.dbConfigForm.get('serverDomain').setValue(null);
    this.dbConfigForm.get('serverDomain').enable();
    this.dbConfigForm.get('serverDomain').setValidators([Validators.required]);
    this.dbConfigForm.get('serverDomain').setErrors({'required': true});
  }

  async ngAfterViewInit() {
    this.dbConfigForm.get('dbServer').setValue('DB2');
    let config = await this.installerService.getConfig();
    this.updateValues(config.db, true);
    this.dbConfigForm.get('configMode').setValue(config.db.configMode);
  }

  updateValues(dbConfig: DBConfig, updateCreds: boolean) {
    if (dbConfig.dbType && dbConfig.host && dbConfig.port && dbConfig.databaseName) {
      this.dbConfigForm.get('dbServer').setValue(dbConfig.dbType);
      this.dbConfigForm.get('hostname').setValue(dbConfig.host);
      this.dbConfigForm.get('port').setValue(dbConfig.port);
      this.dbConfigForm.get('dbSid').setValue(dbConfig.databaseName);
    }
    this.dbConfigForm.get('jdbcUrl').setValue(dbConfig.url);
    if (updateCreds) {
      this.dbConfigForm.get('username').setValue(dbConfig.username);
      this.dbConfigForm.get('password').setValue(dbConfig.password);
    }
    if (!dbConfig.configMode) {
      dbConfig.configMode = this.BASIC_CONFIG_KEY;
    }
    this.dbConfigForm.get('sslConnection').setValue(dbConfig.ssl);
    this.dbConfigForm.get('validateServer').setValue(dbConfig.ssl && dbConfig.validateServerCertificate);
    this.dbConfigForm.get('serverDomain').setValue(dbConfig.hostNameInCertificate);
    let authType = dbConfig.osAuthentication? 'osAuth': 'nativeAuth';
    this.dbConfigForm.get('authType').setValue(authType);
  }

  openDialog(msg: string, status: string): void {
    if (this.dialogRef){
      this.dialogRef.close();
    }
    this.dialogRef = this.dialog.open(PopupDialogComponent, {
      width: '341px',
      height: '167px',
      data: {msg: msg, status: status, confirmDialog: false},
      autoFocus: false
    });
  }

}
