// auth-callback.component.ts
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';

// for google login

@Component({
  selector: 'app-auth-callback',
  template: '<div>Processing authentication...</div>' // Add a template
})
export class AuthCallbackComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    console.log('here')
    const success = this.route.snapshot.queryParamMap.get('success');
    if (success === 'true') {
      console.log("navigate oke")
      this.router.navigate(['/'], { replaceUrl: true });
    } else {
      // Invalid state or access, go to login again or home
      this.router.navigate(['/auth'], { replaceUrl: true });
    }
  }
}
