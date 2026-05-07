import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuth = authService.isAuthenticated();
  console.log('Auth Guard Check:', {
    isAuthenticated: isAuth,
    storedToken: localStorage.getItem('token')?.substring(0, 20) + '...',
    targetRoute: state.url
  });

  if (isAuth) {
    console.log('Access allowed');
    return true;
  } else {
    console.log('Access denied - redirecting to login');
    router.navigate(['/login']);
    return false;
  }
};
