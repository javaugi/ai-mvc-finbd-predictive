import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { authGuard } from './auth.guard'; // Import the guard

export const routes: Routes = [
  // 1. Default Route: Redirects the base path ('') to the login page
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // 2. Login Route: Unprotected
    { path: 'login', component: LoginComponent },
    // You can reuse LoginComponent or create a separate CallbackComponent
    { path: 'auth/callback', component: LoginComponent },

  // 3. Dashboard Route: Protected by the authGuard
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  
  // 4. Wildcard Route (404): Redirects unknown paths to login
  { path: '**', redirectTo: 'login' }
];