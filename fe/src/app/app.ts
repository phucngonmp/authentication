import {Component, OnInit} from '@angular/core';
import {FooterComponent} from './components/footer/footer';
import {Header} from './components/header/header';
import {RouterOutlet} from '@angular/router';
import {AuthService} from './services/auth.service';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-root',
  template: `
    <router-outlet></router-outlet>`,
  imports: [
    RouterOutlet
  ]
})
export class App implements OnInit{

  constructor(public authService: AuthService) {}

  ngOnInit() {
    this.authService.checkLoginStatus().subscribe({
      next: (response: any) => {
        this.authService.setLoginStatus(response.data === "logged in");
        console.log(response)
      },
      error: () => {
        console.log('error occur');
      }
    });
  }
}
