import { Component, HostListener } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Observable} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  imports: [
    AsyncPipe,
    RouterLink
  ],
  styleUrls: ['./header.css']
})
export class Header {
  isLoggedIn$: Observable<boolean>;

  constructor(public authService: AuthService) {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
  }
  menuActive = false;

  toggleMobileMenu() {
    this.menuActive = !this.menuActive;
  }

  closeMobileMenu() {
    this.menuActive = false;
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    if (window.innerWidth >= 768) this.menuActive = false;
  }


  navigateTo(section: string) {
    console.log('Navigate to:', section);
  }

  navigateToMobile(section: string) {
    console.log('Navigate to (mobile):', section);
    this.closeMobileMenu();
  }

  openSearch() {
    console.log('Search opened');
  }

  openSearchMobile() {
    this.closeMobileMenu();
    console.log('Search opened (mobile)');
  }

  openCart() {
    console.log('Cart opened');
  }

}
