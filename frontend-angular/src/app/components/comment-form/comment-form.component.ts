import { Component, Input, Output, EventEmitter } from '@angular/core';
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
  @Input() isSubmitting = false;
  @Output() submitComment = new EventEmitter<string>();

  body = '';

  constructor(public authService: AuthService) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  onSubmit(): void {
    if (!this.body.trim() || this.isSubmitting) return;
    this.submitComment.emit(this.body);
    this.body = '';
  }
}
