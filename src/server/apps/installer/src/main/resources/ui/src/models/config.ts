import {DBConfig} from "./db-config";
import {PortConfig} from "./port-config";
import {ManagementServerConfig} from "./management-server-config";
import {SslConfig} from "./ssl-config";

export class Config {
  public user: string;
  public adminPassword: string;
  public db: DBConfig;
  public managementServer: ManagementServerConfig;
  public licenseFilePath: string;
  public licenseFile: File;
  public sslCertFiles: [File];
  public domain: string;
  public home: string;
  public hostname: string;
  public installationPath: string;
  public port: PortConfig;
  public ssl: SslConfig;
  public previousHome: string;
  public previousVersion: string;
  public runningMode: string;
  public operatingSystem: string;
  public type: string;
  public upgradeExisting: boolean;
  public userGroup: string;
  public version: {
    ccVersion: string;
  };
}
