export class DBConfig {
  public configMode: string;
  public osAuthentication: boolean;
  public password: string;
  public url: string;
  public username: string;
  public databaseName: string;
  public schemaRequired: boolean;
  public schemaName: string;
  public dbType: string;
  public host: string;
  public port: number;
  public ssl: boolean;
  public validateServerCertificate: boolean;
  public hostNameInCertificate: string;
  public retryBackOffPeriod: number;
  public retryAttempts: number;
}
