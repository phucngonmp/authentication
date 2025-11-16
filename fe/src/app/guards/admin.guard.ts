import { Injectable } from '@angular/core';
import {CanActivate, Router, UrlTree} from '@angular/router';
import { AuthService } from '../services/auth.service';
import {map, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    // Ensure we have an access token (refresh via cookie if needed), then check role
    return this.authService.ensureAccessToken().pipe(
      map(() => this.authService.isLoggedAsAdmin()
        ? true
        : this.router.createUrlTree(['/'])
      )
    );
  }
}
