import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Article } from '../../models';
import { ArticlesService } from '../../services/articles.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-article-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './article-card.component.html'
})
export class ArticleCardComponent {
  @Input() article!: Article;
  @Output() updated = new EventEmitter<Article>();

  isLoading = false;

  constructor(
    private articlesService: ArticlesService,
    public authService: AuthService
  ) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  handleFavorite(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (!this.authService.currentUser || this.isLoading) return;

    this.isLoading = true;
    const action = this.article.favorited
      ? this.articlesService.unfavoriteArticle(this.article.slug)
      : this.articlesService.favoriteArticle(this.article.slug);

    action.subscribe({
      next: (updatedArticle) => {
        this.updated.emit(updatedArticle);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  }
}
