import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Config} from "../models/config";
import {DBConfig} from "../models/db-config";
import {ProgressMessage, Task} from "../models/progress-message";
import {SERVER_URL} from "./installer-constants";

@Injectable({
  providedIn: 'root'
})
export class InstallerService {

  config: Config;
  authHeader: HttpHeaders;
  installStarted: boolean = false;
  installExited: boolean = false;

  constructor(private http: HttpClient) {
  }

  setKey(key: string) {
    let basicAuth = btoa(`cc-user:${key}`);
    sessionStorage.setItem("key", basicAuth);
    let headers = new HttpHeaders();
    this.authHeader = headers.append("Authorization", `Basic ${basicAuth}`);
  }

  getBasicAuthHeader() {
    if (!this.authHeader) {
      // on browser refresh, load from sessionStorage
      let basicAuth = sessionStorage.getItem("key");
      let headers = new HttpHeaders();
      this.authHeader = headers.append("Authorization", `Basic ${basicAuth}`);
    }
    return this.authHeader;
  }

  private getServerConfig() {
    return this.http.get<Config>(`${SERVER_URL}/installation/properties`);
  }

  getTasks() {
    return this.http.get<string[]>(`${SERVER_URL}/installation/progressMileStones`);
  }

  getCurrentTask() {
    return this.http.get<ProgressMessage>(`${SERVER_URL}/installation/progress`);
  }

  createMarker() {
    return this.http.get(`${SERVER_URL}/installation/startCC`);
  }

  exit() {
    return this.http.get(`${SERVER_URL}/installation/exit`);
  }

  getDbDetails(jdbcUrl: string) {
    const endpoint = `${SERVER_URL}/installation/getDbDetails`;
    return this.http
      .post<DBConfig>(endpoint, jdbcUrl);
  }

  getJdbcUrl(dbDetails: DBConfig): any {
    const endpoint = `${SERVER_URL}/installation/getJdbcUrl`;
    return this.http
      .post(endpoint, dbDetails);
  }

  deleteCACertificate(certName: string) {
    const endpoint = `${SERVER_URL}/installation/deleteCACertificate?caCertificateFileName=${certName}`;
    return this.http.delete(endpoint);
  }

  deleteLicense() {
    const endpoint = `${SERVER_URL}/installation/deleteLicense`;
    return this.http.delete(endpoint);
  }

  getCACertificates(): any {
    return this.http.get(`${SERVER_URL}/installation/listCACertificates`);
  }

  uploadLicense(licenseFile: File) {
    const endpoint = `${SERVER_URL}/installation/uploadLicense`;
    const formData: FormData = new FormData();
    formData.append('license', licenseFile, licenseFile.name);
    return this.http
      .post(endpoint, formData);
  }

  uploadCACertificate(dbCertificate: File) {
    const endpoint = `${SERVER_URL}/installation/uploadCACertificate`;
    const formData: FormData = new FormData();
    formData.append('caCertificate', dbCertificate, dbCertificate.name);
    return this.http
      .post(endpoint, formData);
  }

  uploadConfig() {
    const endpoint = `${SERVER_URL}/installation/properties`;
    return this.http
      .put(endpoint, this.config);
  }

  validateDbConnection() {
    const endpoint = `${SERVER_URL}/installation/validateDbConnection`;
    return this.http
      .post(endpoint, this.config.db);
  }

  validatePorts() {
    const endpoint = `${SERVER_URL}/installation/validatePorts`;
    return this.http
      .post(endpoint, this.config.port);
  }

  validateInstallationPath(installationPath: string) {
    const endpoint = `${SERVER_URL}/installation/validateInstallationPath`;
    return this.http
      .post(endpoint, installationPath);
  }

  installControlCentre() {
    const endpoint = `${SERVER_URL}/installation/install`;
    this.installStarted = true;
    return this.http
      .post(endpoint, this.config);
  }

  // if config not already fetched from server, fetch from server
  async getConfig(): Promise<Config> {
    if (!this.config) {
      const config = await this.getServerConfig().toPromise();
      let splits = config.licenseFilePath && config.licenseFilePath.split(/[\\/]/);
      let licenseFileName = splits && splits[splits.length - 1];

      // not using File constructor to support IE11
      if(licenseFileName) {
        // @ts-ignore
        config.licenseFile = {'name': licenseFileName};
      }
      let sslCertFileNames = await this.getCACertificates().toPromise();
      if(sslCertFileNames) {
        // @ts-ignore
        config.sslCertFiles = sslCertFileNames.map((fileName) => { return {'name': fileName}; });
      }
      this.setConfig(config);
    }
    return new Promise((resolve) => {
      resolve(this.config);
    });
  }

  async getType(): Promise<string>{
    let type = (await this.getConfig()).type;
    if (type === 'COMPLETE' || type === 'MANAGEMENT_SERVER' || type === 'ICENET'){
      return type;
    } else {
      let components = type.split(',');
      if (components.includes('DABS') && components.includes('DMS')){
        return 'COMPLETE';
      } else if (components.includes('DMS')){
        return 'MANAGEMENT_SERVER';
      } else {
        return 'ICENET';
      }
    }
  }

  setConfig(config: Config) {
    this.config = config;
  }

  setLicenseFile(licenseFile: File){
    this.config.licenseFile = licenseFile;
  }

  setSslCertFiles(sslCertFiles: [File]){
    this.config.sslCertFiles = sslCertFiles;
  }

  setInstallationType(installationType: string) {
    this.config.type = installationType;
  }

  setSuperUserCredentials(password: string) {
    this.config.adminPassword = password;
  }

  setDbConfig(dbConfig: DBConfig) {
    dbConfig.retryBackOffPeriod = this.config.db.retryBackOffPeriod;
    dbConfig.retryAttempts = this.config.db.retryAttempts;
    this.config.db = dbConfig;
  }

  setManagementServerConfig(host: string, webServicePort: number, configServicePort: number) {
    this.config.managementServer.host = host;
    this.config.managementServer.webServicePort = webServicePort;
    this.config.managementServer.configServicePort = configServicePort;
  }

  setKeyStoreTrustStorePassword(keyStorePassword: string, trustStorePassword: string) {
    this.config.ssl.keystore.password = keyStorePassword;
    this.config.ssl.truststore.password = trustStorePassword;
  }

  setPorts(appServicePort: number, policyValidatorPort: number, configServicePort: number, webServicePort: number, activeMqPort: number) {
    this.config.port.appServicePort = appServicePort;
    this.config.port.policyValidatorPort = policyValidatorPort;
    this.config.port.configServicePort = configServicePort;
    this.config.port.webServicePort = webServicePort;
    this.config.port.activeMqPort = activeMqPort;
  }

  setInstallationPath(installDirectory: string){
    this.config.installationPath = installDirectory;
  }

  setUpgradeExisting(upgradeExisting: boolean){
    this.config.upgradeExisting = upgradeExisting;
  }
}
