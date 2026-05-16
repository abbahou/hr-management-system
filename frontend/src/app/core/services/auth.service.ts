import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, BehaviorSubject } from 'rxjs';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  userId: string;
  email: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private authSubject = new BehaviorSubject<boolean>(this.validateToken());
  public auth$ = this.authSubject.asObservable();

  private readonly TOKEN_KEY = 'token';
  private readonly TOKEN_EXPIRY_KEY = 'token_expiry';

  constructor(private http: HttpClient) {
    this.initializeAuth();
  }

  /**
   * Initialize auth on app startup - cleanup invalid tokens
   */
  private initializeAuth(): void {
    console.log('🔐 Initializing auth...');

    // Clean up any expired tokens
    if (!this.validateToken()) {
      console.log('⏳ Token expired or invalid, clearing...');
      this.clearAuth();
    } else {
      console.log('✅ Valid token found');
    }

    // Set up token expiry timer
    this.setupTokenExpiryTimer();
  }

  /**
   * Validate JWT token
   */
  private validateToken(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);

    if (!token) {
      return false;
    }

    try {
      // Decode JWT (without verification - verification happens on server)
      const decoded = this.decodeToken(token);

      if (!decoded) {
        console.warn('❌ Token decode failed');
        return false;
      }

      // Check expiration
      const expiryTime = decoded.exp * 1000; // Convert to milliseconds
      const now = Date.now();

      if (now > expiryTime) {
        console.warn('⏳ Token expired');
        return false;
      }

      // Token is valid
      const remainingTime = Math.round((expiryTime - now) / 1000);
      console.log(`✅ Token valid, expires in ${remainingTime}s`);
      return true;
    } catch (error) {
      console.error('❌ Token validation error:', error);
      return false;
    }
  }

  /**
   * Decode JWT token (without verification)
   */
  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64).split('').map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Token decode failed:', error);
      return null;
    }
  }

  /**
   * Setup token expiry timer - auto logout when token expires
   */
  private setupTokenExpiryTimer(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);

    if (!token) {
      return;
    }

    try {
      const decoded = this.decodeToken(token);
      if (!decoded || !decoded.exp) {
        return;
      }

      const expiryTime = decoded.exp * 1000;
      const now = Date.now();
      const timeUntilExpiry = expiryTime - now;

      if (timeUntilExpiry > 0) {
        // Set timeout to logout before expiry (5 minutes before)
        const logoutTime = timeUntilExpiry - (5 * 60 * 1000);

        if (logoutTime > 0) {
          setTimeout(() => {
            console.warn('⚠️  Token expiring soon, logging out...');
            this.logout();
            window.location.reload();
          }, logoutTime);
        }
      }
    } catch (error) {
      console.error('Error setting up token expiry timer:', error);
    }
  }

  /**
   * Pings the public health endpoint
   */
  checkHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/auth/health`);
  }

  /**
   * Submits credentials to the backend to retrieve a JWT Token
   */
  login(credentials: any): Observable<LoginResponse> {
    console.log('🔓 Login attempt...');

    // Clear any existing tokens first
    this.clearAuth();

    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials)
      .pipe(
        tap(response => {
          console.log('✅ Login response received');

          if (response && response.accessToken) {
            console.log('💾 Storing token...');
            localStorage.setItem(this.TOKEN_KEY, response.accessToken);

            // Calculate and store expiry time
            const expiryTime = new Date().getTime() + (response.expiresIn * 1000);
            localStorage.setItem(this.TOKEN_EXPIRY_KEY, expiryTime.toString());

            // Update auth state
            this.authSubject.next(true);

            // Setup expiry timer
            this.setupTokenExpiryTimer();

            console.log('✅ Login successful');
          }
        }),
        catchError(error => {
          console.error('❌ Login failed:', error);
          this.clearAuth();
          throw error;
        })
      );
  }

  /**
   * Registers a new user
   */
  register(userData: any): Observable<LoginResponse> {
    console.log('📝 Registration attempt...');

    // Clear any existing tokens first
    this.clearAuth();

    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/register`, userData)
      .pipe(
        tap(response => {
          if (response && response.accessToken) {
            console.log('💾 Storing token...');
            localStorage.setItem(this.TOKEN_KEY, response.accessToken);

            // Calculate and store expiry time
            const expiryTime = new Date().getTime() + (response.expiresIn * 1000);
            localStorage.setItem(this.TOKEN_EXPIRY_KEY, expiryTime.toString());

            // Update auth state
            this.authSubject.next(true);

            // Setup expiry timer
            this.setupTokenExpiryTimer();

            console.log('✅ Registration successful');
          }
        }),
        catchError(error => {
          console.error('❌ Registration failed:', error);
          this.clearAuth();
          throw error;
        })
      );
  }

  /**
   * Logout - clear token and auth state
   */
  logout(): void {
    console.log('🚪 Logging out...');
    this.clearAuth();
    this.authSubject.next(false);
  }

  /**
   * Clear all auth data
   */
  private clearAuth(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_EXPIRY_KEY);
    console.log('🧹 Auth cleared');
  }

  /**
   * Checks if user is authenticated with valid token
   */
  isAuthenticated(): boolean {
    const isValid = this.validateToken();

    if (!isValid) {
      // If token is invalid but still in storage, clear it
      if (localStorage.getItem(this.TOKEN_KEY)) {
        this.clearAuth();
      }
    }

    return isValid;
  }

  /**
   * Get current token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Get user info from token
   */
  getCurrentUser(): any {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      const decoded = this.decodeToken(token);
      return {
        email: decoded.email,
        role: decoded.role,
        uid: decoded.uid
      };
    } catch (error) {
      console.error('Error getting current user:', error);
      return null;
    }
  }
}

