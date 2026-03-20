import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-comment-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comment-form.component.html'
})
export class CommentFormComponent {
  @Output() submitComment = new EventEmitter<string>();

  body = '';
  isSubmitting = false;

  constructor(public authService: AuthService) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  onSubmit(): void {
    if (!this.body.trim() || this.isSubmitting) return;

    this.isSubmitting = true;
    this.submitComment.emit(this.body);
    this.body = '';
    this.isSubmitting = false;
  }
}
