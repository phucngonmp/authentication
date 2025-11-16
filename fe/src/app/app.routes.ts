import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {HomeComponent} from './components/home/home';
import {AuthComponent} from './components/auth/auth.component';
import {AuthCallbackComponent} from './components/auth/AuthCallbackComponent';
import {ProductsComponent} from './components/products/products';
import {Dashboard} from './components/admin/dashboard/dashboard';
import {AdminGuard} from './guards/admin.guard';
import {Profile} from './components/profile/profile';
import {MainLayout} from './components/layout/mainlayout/mainlayout';
import {AdminLayout} from './components/layout/admin-layout/admin-layout';

export const routes: Routes = [

  {
    path: '' ,
    component: MainLayout,
    children: [
      { path: '', component: HomeComponent},
      { path: 'auth', component: AuthComponent,},
      { path: 'products', component: ProductsComponent},
      { path: 'profile', component: Profile}
    ]
  },
  { path: 'auth/callback', component: AuthCallbackComponent},
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [AdminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full'},
      { path: 'dashboard', component: Dashboard },
      // ... admin pages
    ]
  },

]


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
