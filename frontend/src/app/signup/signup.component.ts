import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html'
})
export class SignupComponent {
  signupForm: FormGroup;
  showPassword = false;
  isLoading = false;
  signupError = '';

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.signupForm.valid) {
      this.isLoading = true;
      this.signupError = '';

      const userData = {
        email: this.signupForm.value.email,
        password: this.signupForm.value.password,
        role: 'EMPLOYEE'
      };

      this.authService.register(userData).subscribe({
        next: () => {
          this.isLoading = false;
          console.log('Signup Successful, navigating to dashboard.');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.isLoading = false;
          this.signupError = 'Registration failed. Please try again.';
          console.error('Signup Failed', err);
        }
      });
    } else {
      // Trigger validation messages on submit
      this.signupForm.markAllAsTouched();
    }
  }
}
