// footer.component.ts
import { Component } from '@angular/core';
import {faFacebook, faTiktok} from '@fortawesome/free-brands-svg-icons';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faInstagram} from '@fortawesome/free-brands-svg-icons/faInstagram';

@Component({
  selector: 'app-footer',
  templateUrl: 'footer.html',
  imports: [
    FaIconComponent
  ],
  styleUrls: ['footer.css']
})
export class FooterComponent {
  currentYear: number = new Date().getFullYear();

  // Social media links (you can customize these)
  socialLinks = [
    { name: 'Instagram', url: '#', icon: faInstagram},
    { name: 'Facebook', url: 'https://www.facebook.com/SangGardenn', icon: faFacebook },
    { name: 'Tiktok', url: 'https://www.tiktok.com/@sanggardenn', icon: faTiktok },
  ];

  // Quick links
  quickLinks = [
    { name: 'About Us', url: '/about' },
    { name: 'Contact', url: '/contact' },
    { name: 'Privacy Policy', url: '/privacy' },
    { name: 'Terms of Service', url: '/terms' }
  ];

  // Footer sections
  companyInfo = {
    name: 'Sang Garden',
    description: 'The beauty of plants',
    email: 'ngominhsang20112000@gmail.com',
    phone: '090 577 08 56'
  };
}
