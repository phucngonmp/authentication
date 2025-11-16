import {Component, Injectable} from '@angular/core';
import { Router } from '@angular/router';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule, ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import {AppConstants} from '../../common/AppConstants';
import {AuthService} from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'auth-component',
  styleUrl: 'auth.css',
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: 'auth.html'
})
@Injectable({ providedIn: 'root' })
export class AuthComponent {

  constructor(private authService: AuthService,  private router: Router) {
  }
  // to switch login and register
  currentTab: 'login' | 'register' = 'login';
  switchTab(tab: 'login' | 'register') {
    this.currentTab = tab;
    this.loginForm.reset();
    this.registerForm.reset();
    this.showPassword = false;
    this.errorMessage = "";
  }

  showPassword = false;
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  // login form zone for validation
  loginForm = new FormGroup({
      identifier : new FormControl('', [Validators.required]),
      password : new FormControl('', [Validators.required])
  });
  get loginIdentifier(){
    return this.loginForm.get('identifier');
  }
  get loginPassword(){
    return this.loginForm.get('password');
  }

  // register form zone for validation
  registerForm = new FormGroup({
    username : new FormControl('', [Validators.required]),
    email : new FormControl('', [
      Validators.required,
      Validators.email]),
    password : new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(/^(?=.*[!@#$%^&*(),.?":{}|<>])(?=.*\d).+$/)]),
    rePassword : new FormControl('', [Validators.required]),
  }, { validators: misMatchPasswordValidator() }); // Apply to FormGroup, not individual control

  get registerUsername() {
    return this.registerForm.get('username');
  }
  get registerEmail() {
    return this.registerForm.get('email');
  }
  get registerPassword() {
    return this.registerForm.get('password');
  }
  get registerRePassword() {
    return this.registerForm.get('rePassword');
  }


  // handle submit buttons
  submitLogin(){
    const { identifier, password} = this.loginForm.value;
    this.authService.login(identifier ?? '', password ?? '').subscribe({
      next: (res) =>{
       console.log(res)
        if(res.success === true){
          let role = res.data.role;
          this.authService.setLoginStatus(true);
          this.authService.setAccessToken(res.data.accessToken);
          console.log(this.authService.getAccessToken());
          console.log(role)
          if(role === 'ADMIN'){
            this.router.navigate(['/admin/dashboard']);
          } else {
            this.router.navigate(['/']);
          }
        } else{
          this.errorMessage = res;
        }

      },
      error:(err) =>{
        this.errorMessage = err?.error?.message ?? 'Something went wrong';
        console.log(this.errorMessage)
      }
    });
  }

  errorMessage : string = "";

  clearApiError(){
    this.errorMessage = '';
  }

  submitRegister() {
    const { username, password, email } = this.registerForm.value;

    this.authService.register(username ?? '', password ?? '', email ?? '').subscribe({
      next: (res) => {
        console.log('Register successful', res);
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Register failed', err);
        this.errorMessage = err?.error?.message ?? 'Something went wrong';
        console.log(this.errorMessage)
      }
    });
  }


  loginWithGoogle(){
    window.location.href = AppConstants.GOOGLE_AUTH_URL;
  }

}

// for checking mismatch password and confirm password in register form
export function misMatchPasswordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password')?.value;
    const confirm = control.get('rePassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  };
}
