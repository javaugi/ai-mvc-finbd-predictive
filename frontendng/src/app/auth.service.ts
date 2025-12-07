import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common'; // New import
import { HttpClient } from '@angular/common/http'; // New Import
import { Observable, tap, throwError } from 'rxjs'; // New Imports for Observable handling

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private router = inject(Router);
    private platformId = inject(PLATFORM_ID); // Inject Platform ID
    private http = inject(HttpClient); // Inject HttpClient

    // Base URL of your Spring Boot/OAuth2 backend
    private API_BASE_URL = 'http://localhost:8088/'; // Changed to base URL
    private ACCESS_TOKEN_KEY = 'token';
    //private API_BASE_URL = 'http://localhost:8088/oauth2/authorization/';
    //private ACCESS_TOKEN_KEY = 'access_token';

    constructor() {
        // Only run the callback handling if we are in the browser
        if (isPlatformBrowser(this.platformId)) {
            this.handleAuthenticationCallback();
        }
    }

    // Check if a token exists in local storage
    isAuthenticated(): boolean {
        // Wrap local storage access
        if (isPlatformBrowser(this.platformId)) {
            const token = localStorage.getItem(this.ACCESS_TOKEN_KEY);
            return !!token;
        }
        return false;
    }

    // Existing: OAuth2 Redirects (Gmail, GitHub)
    initiateLogin(provider: string): void {
        if (isPlatformBrowser(this.platformId)) {
            // Note: The /oauth2/authorization/ prefix is specific to Spring Security OAuth2 redirects
            window.location.href = this.API_BASE_URL + 'oauth2/authorization/' + provider + '?redirect_uri=http://localhost:4200/auth/callback';
        }
    }

    // NEW: Handles Internal User Login via form submission
    loginInternalUser(email: string, password: string): Observable<any> {
        const url = this.API_BASE_URL + 'auth/login'; // Your designated internal login endpoint
        const body = { email, password };

        return this.http.post<any>(url, body).pipe(
            tap(response => {
                // Assuming your /auth/login returns a JSON body with a token property
                const token = response.token;
                if (token) {
                    this.setToken(token);
                    this.router.navigate(['/dashboard']);
                } else {
                    // Handle cases where the server returns 200 but no token
                    throw new Error('Authentication failed: No token received.');
                }
            }),
            // Use catchError to handle HTTP error responses (e.g., 401 Unauthorized)
            // It's cleaner to handle errors in the component, but good practice to map them here.
        );
    }

    // ... (isAuthenticated, handleAuthenticationCallback, setToken, logout methods remain the same)    
    // Note: The rest of the methods (isAuthenticated, handleAuthenticationCallback, setToken, logout)
    // should remain as you defined them in the previous response, but ensure they include
    // the `isPlatformBrowser` checks if you are using SSR.

    // Handles the URL after the backend redirects the user back
    private handleAuthenticationCallback(): void {
        // This function is now ONLY called in the browser environment (from the constructor)
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if (token) {
            this.setToken(token);
            this.router.navigate(['/dashboard']);
        }
    }

    setToken(token: string): void {
        // Wrap local storage access
        if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
        }
    }

    logout(): void {
        // Wrap local storage access
        if (isPlatformBrowser(this.platformId)) {
            localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        }
        this.router.navigate(['/login']);
    }
}