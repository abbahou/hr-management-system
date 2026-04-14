import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
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

  constructor(private http: HttpClient) { }

  /**
   * Pings the public health endpoint
   */
  checkHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/auth/health`);
  }

  /**
   * Submits credentials to the backend to retrieve a JWT Token
   * Assuming payload structure: { email: string; password: string }
   */
  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response && response.accessToken) {
            localStorage.setItem('token', response.accessToken);
          }
        })
      );
  }

  /**
   * Registers a new user
   */
  register(userData: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/register`, userData)
      .pipe(
        tap(response => {
          if (response && response.accessToken) {
            localStorage.setItem('token', response.accessToken);
          }
        })
      );
  }

  /**
   * Removes JWT Token from local storage
   */
  logout(): void {
    localStorage.removeItem('token');
  }

  /**
   * Checks if user has a token in storage
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}
