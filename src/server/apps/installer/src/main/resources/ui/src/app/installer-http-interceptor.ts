import {Injectable} from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpHeaders
} from '@angular/common/http';

import {Observable} from 'rxjs';
import {InstallerService} from "./installer.service";

@Injectable()
export class InstallerHttpInterceptor implements HttpInterceptor {

  constructor(private installerService: InstallerService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.installerService.getBasicAuthHeader()) {
      let headers = this.installerService.getBasicAuthHeader();
      headers = headers.append("X-Requested-With","XMLHttpRequest");
      request = request.clone({headers: headers});
    }
    return next.handle(request);
  }
}
