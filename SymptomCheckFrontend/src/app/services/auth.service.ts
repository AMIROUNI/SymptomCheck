import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, from } from 'rxjs';
import { tap, switchMap } from 'rxjs/operators';
import { User, UserRole } from '../models/user.model';
import { UserRegistrationRequest } from '../models/auth-request.model';
import { KeycloakService } from 'keycloak-angular';
import { Router } from '@angular/router';
import { DoctorProfileStatusDTO } from '../models/doctor-profile-status.model';

const apiUrl = 'http://userservice/api/v1/users';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();


  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService,
    private router: Router,

  ) {
    this.loadCurrentUser();
  }

  public loadCurrentUser() {
    // Check if user is authenticated in Keycloak
    if (this.keycloakService.isLoggedIn()) {
      this.keycloakService.getToken().then(token => {
        console.log('✅ Access Token:', token);

        // Fetch user details from backend
        this.http.get<User>(`${apiUrl}/me`).subscribe({
          next: user => {
            console.log('✅ Current User:', user);
            this.currentUserSubject.next(user);
          },
          error: err => {
            console.error('❌ Error fetching user:', err);
            this.currentUserSubject.next(null);
          }
        });
      });
    }
  }


  /**
   * Register a new user (this doesn't require Keycloak login)
   */
  registerUser(userData: any, file: File | null) {
    const formData = new FormData();
    formData.append('user', new Blob([JSON.stringify(userData)], { type: 'application/json' }));

    if (file) {
      formData.append('file', file);
    }

    return this.http.post(`${apiUrl}/register`, formData);
  }

  /**
   * Login using Keycloak (redirects to Keycloak or uses custom login)
   * Option 1: Redirect to Keycloak login page
   */
  loginWithRedirect(): void {
    this.keycloakService.login({
      redirectUri: window.location.origin + '/dashboard'
    });
  }

  /**
   * Login using custom form with Keycloak's Direct Access Grant
   * This allows you to use your custom login page while still using Keycloak tokens
   */
  async loginWithCredentials(username: string, password: string): Promise<void> {
    try {
      // Call Keycloak token endpoint directly
      const response = await fetch('http://localhost:8080/realms/symptomcheck-realm/protocol/openid-connect/token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          client_id: 'angular-client',
          grant_type: 'password',
          username: username,
          password: password,
          scope: 'openid'
        })
      });

      if (!response.ok) {
        throw new Error('Invalid credentials');
      }

      const data = await response.json();
      console.log('✅ Login successful, tokens received:', data);

      // Store tokens in Keycloak service (it handles them internally, no localStorage needed by you)
      // Keycloak service will automatically use these tokens for subsequent requests
      await this.keycloakService.init({
        config: {
          url: 'http://localhost:8080',
          realm: 'symptomcheck-realm',
          clientId: 'angular-client',
        },
        initOptions: {
          token: data.access_token,
          refreshToken: data.refresh_token,
          checkLoginIframe: false,
        },
        enableBearerInterceptor: true,
        bearerExcludedUrls: ['/assets', '/api/v1/users/register', '/api/v1/auth']
      });

      // Load current user after successful login
      this.loadCurrentUser();

      // Navigate to dashboard
      this.router.navigate(['/dashboard']);

    } catch (error) {
      console.error('❌ Login failed:', error);
      throw error;
    }
  }

  /**
   * Logout from Keycloak
   */
  logout(): void {
    this.keycloakService.logout(window.location.origin);
    this.currentUserSubject.next(null);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.keycloakService.isLoggedIn();
  }


  getUserRoles(): string[] {
  return this.keycloakService?.getUserRoles(true) || [];
}

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Get access token for manual API calls
   */
  async getAccessToken(): Promise<string> {
    return await this.keycloakService.getToken();
  }
}
