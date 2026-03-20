import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
  image = '';
  username = '';
  bio = '';
  email = '';
  password = '';
  error = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const user = this.authService.currentUser;
    if (user) {
      this.image = user.image || '';
      this.username = user.username;
      this.bio = user.bio || '';
      this.email = user.email;
    }
  }

  onSubmit(): void {
    this.error = '';
    this.isLoading = true;

    const updateData: any = {
      image: this.image || undefined,
      username: this.username,
      bio: this.bio || undefined,
      email: this.email
    };
    if (this.password) {
      updateData.password = this.password;
    }

    this.authService.updateUser(updateData).subscribe({
      next: () => {
        this.router.navigate(['/profile', this.username]);
      },
      error: (err) => {
        const errors = err.error?.errors;
        if (errors) {
          this.error = Object.entries(errors)
            .map(([field, messages]: [string, any]) => `${field}: ${(messages as string[]).join(', ')}`)
            .join('; ');
        } else {
          this.error = 'Failed to update profile';
        }
        this.isLoading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
