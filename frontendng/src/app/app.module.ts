// app.module.ts (or main app file if standalone components)
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; // New Import
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; // <-- Required for JsonPipe and *ngIf/For
import { provideRouter, RouterModule } from '@angular/router';
import { routes } from './app.routes'; // Assuming your routes are in app.routes.ts

import { App } from './app';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';

@NgModule({
    declarations: [
        //App,
        //LoginComponent,
        //DashboardComponent
    ],
    // Other modules whose exported classes are needed by component templates declared in this module.
    imports: [
        BrowserModule, // Provides functionality for running app in the browser
        FormsModule,
        //HttpClient, // Required for making API calls in AuthService
        ReactiveFormsModule, // Required for formGroup and formControlName in LoginComponent
        CommonModule, // Typically included indirectly via BrowserModule, but harmless to list explicitly
        // If you were using RouterModule.forRoot(routes) instead of provideRouter in main.ts
        RouterModule.forRoot(routes) 
    ],
    // Services that the module contributes to the global collection of services.
    providers: [
        // This provides the router configuration using the modern function approach
        provideRouter(routes)
    ],
    // The main component that bootstraps the application
    bootstrap: [App]
})
export class AppModule { }