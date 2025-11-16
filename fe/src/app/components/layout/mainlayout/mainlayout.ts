import { Component } from '@angular/core';
import {Header} from '../../header/header';
import {FooterComponent} from '../../footer/footer';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-main-layout',
  imports: [
    Header,
    FooterComponent,
    RouterOutlet
  ],
  template: `
    <app-header></app-header>
    <router-outlet></router-outlet>
    <app-footer></app-footer>
  `
})
export class MainLayout {

}
