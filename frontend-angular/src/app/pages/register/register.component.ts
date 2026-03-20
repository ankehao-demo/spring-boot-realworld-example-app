import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  error = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.error = '';
    this.isLoading = true;

    this.authService.register(this.username, this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        const errors = err.error?.errors;
        if (errors) {
          this.error = Object.entries(errors)
            .map(([field, messages]: [string, any]) => `${field}: ${(messages as string[]).join(', ')}`)
            .join('; ');
        } else {
          this.error = 'Registration failed';
        }
        this.isLoading = false;
      }
    });
  }
}
