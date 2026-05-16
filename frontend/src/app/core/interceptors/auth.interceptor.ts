import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Retrieve the stored JWT token
  const token = authService.getToken();

  // Clone the request and inject the Bearer token in the headers
  if (token) {
    console.log('🔐 Injecting token into request');
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          console.error('❌ 401 Unauthorized - token invalid or expired');
          authService.logout();
          router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }

  // If no token exists, proceed with the original request
  return next(req);
};
