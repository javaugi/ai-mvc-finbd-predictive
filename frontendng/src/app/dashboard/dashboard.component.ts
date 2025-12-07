import { Component, inject } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {
    private authService = inject(AuthService);

    logout(): void {
        this.authService.logout();
    }
}
