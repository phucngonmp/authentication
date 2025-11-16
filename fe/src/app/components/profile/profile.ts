import { Component } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})

export class Profile {
  constructor(
    private authService: AuthService,
    private  route: Router
  ) {}

    logout() {
      this.authService.logout().subscribe(
        {
          next: response => {
            console.log(response);
            if(response.success === true) {
              this.authService.setLoginStatus(false)
              this.route.navigate(['/auth']);
            }
            else{
              console.log('something went wrong')
            }
          },
          error:(err) =>{
            let error: string = err?.error?.message ?? 'Something went wrong';
            console.log(error)
          }
        }
      );
    }
}
