import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Retrieve the stored JWT token
  const token = localStorage.getItem('token');

  // Clone the request and inject the Bearer token in the headers
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  // If no token exists, proceed with the original request
  return next(req);
};
