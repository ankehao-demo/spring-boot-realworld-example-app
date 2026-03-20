import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Comment } from '../../models';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-comment-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './comment-list.component.html'
})
export class CommentListComponent {
  @Input() comments: Comment[] = [];
  @Output() deleteComment = new EventEmitter<string>();

  constructor(public authService: AuthService) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  }

  onDelete(commentId: string): void {
    this.deleteComment.emit(commentId);
  }
}
