import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; // <-- Import ReactiveFormsModule here
import { CommonModule, JsonPipe } from '@angular/common'; // Also often needed for directives like *ngIf
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
    standalone: true, // Assuming standalone
    imports: [
        ReactiveFormsModule, // Fixes NG8002: Can't bind to 'formGroup'
        CommonModule,         // Provides *ngIf, *ngFor
        JsonPipe              // Fixes NG8004: No pipe found with name 'json' (if needed in template)
    ],
})
export class LoginComponent implements OnInit {
    private authService = inject(AuthService);
    private fb = inject(FormBuilder); // Inject FormBuilder

    // Define the form group
    loginForm!: FormGroup;

    // Track if a login attempt is in progress (for UI feedback)
    isLoading: boolean = false;
    loginError: string | null = null;

    ngOnInit(): void {
        // Initialize the form with validation rules
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
        });
    }

    // Method for OAuth2 redirects (Gmail, GitHub) - remains the same
    loginWithProvider(provider: 'google' | 'github' | 'epicfhir'): void {
        this.authService.initiateLogin(provider);
    }

    // NEW METHOD for internal user login via form submit
    handleInternalLogin(): void {
        this.loginError = null; // Clear previous errors

        // Check if the form is valid based on Validators
        if (this.loginForm.invalid) {
            this.loginError = 'Please check your email and password format.';
            return;
        }

        this.isLoading = true;
        const { email, password } = this.loginForm.value;

        // Call the updated service method
        this.authService.loginInternalUser(email, password).subscribe({
            next: (success) => {
                // Service handles saving token and navigation on success
                this.isLoading = false;
            },
            error: (err) => {
                this.isLoading = false;
                // Display a user-friendly error message
                this.loginError = 'Login failed. Please check your credentials.';
                console.error('Internal Login Error:', err);
            },
        });
    }
}
