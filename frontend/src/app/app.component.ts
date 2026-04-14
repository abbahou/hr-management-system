import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'font-end';

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.checkHealth().subscribe({
      next: (res) => console.log('API Health Check OK:', res),
      error: (err) => console.error('API Health Check FAILED:', err)
    });
  }
}
