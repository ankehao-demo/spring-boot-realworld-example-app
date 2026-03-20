import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { CommentsService } from '../../services/comments.service';
import { ProfilesService } from '../../services/profiles.service';
import { AuthService } from '../../services/auth.service';
import { Article, Comment } from '../../models';
import { CommentFormComponent } from '../../components/comment-form/comment-form.component';
import { CommentListComponent } from '../../components/comment-list/comment-list.component';

@Component({
  selector: 'app-article-view',
  standalone: true,
  imports: [CommonModule, RouterModule, CommentFormComponent, CommentListComponent],
  templateUrl: './article-view.component.html'
})
export class ArticleViewComponent implements OnInit {
  article: Article | null = null;
  comments: Comment[] = [];
  isLoading = true;
  isFollowing = false;
  isFollowLoading = false;
  isCommentSubmitting = false;

  constructor(
    private articlesService: ArticlesService,
    private commentsService: CommentsService,
    private profilesService: ProfilesService,
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  get isAuthor(): boolean {
    return !!this.authService.currentUser && !!this.article &&
      this.authService.currentUser.username === this.article.author.username;
  }

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug');
    if (slug) {
      this.loadArticle(slug);
      this.loadComments(slug);
    }
  }

  loadArticle(slug: string): void {
    this.articlesService.getArticle(slug).subscribe({
      next: (article) => {
        this.article = article;
        this.isFollowing = article.author.following;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  loadComments(slug: string): void {
    this.commentsService.getComments(slug).subscribe({
      next: (comments) => this.comments = comments,
      error: (err) => console.error('Error loading comments:', err)
    });
  }

  handleFavorite(): void {
    if (!this.article || !this.authService.currentUser) return;

    const action = this.article.favorited
      ? this.articlesService.unfavoriteArticle(this.article.slug)
      : this.articlesService.favoriteArticle(this.article.slug);

    action.subscribe({
      next: (updatedArticle) => this.article = updatedArticle,
      error: (err) => console.error('Error toggling favorite:', err)
    });
  }

  handleFollow(): void {
    if (!this.article || !this.authService.currentUser || this.isFollowLoading) return;

    this.isFollowLoading = true;
    const action = this.isFollowing
      ? this.profilesService.unfollowUser(this.article.author.username)
      : this.profilesService.followUser(this.article.author.username);

    action.subscribe({
      next: (profile) => {
        this.isFollowing = profile.following;
        if (this.article) {
          this.article = { ...this.article, author: { ...this.article.author, following: profile.following } };
        }
        this.isFollowLoading = false;
      },
      error: () => this.isFollowLoading = false
    });
  }

  handleDelete(): void {
    if (!this.article || !this.isAuthor) return;
    if (confirm('Are you sure you want to delete this article?')) {
      this.articlesService.deleteArticle(this.article.slug).subscribe({
        next: () => this.router.navigate(['/']),
        error: (err) => console.error('Error deleting article:', err)
      });
    }
  }

  onCommentSubmit(body: string): void {
    if (!this.article) return;
    this.isCommentSubmitting = true;
    this.commentsService.addComment(this.article.slug, body).subscribe({
      next: (comment) => {
        this.comments = [comment, ...this.comments];
        this.isCommentSubmitting = false;
      },
      error: (err) => {
        console.error('Error adding comment:', err);
        this.isCommentSubmitting = false;
      }
    });
  }

  onCommentDelete(commentId: string): void {
    if (!this.article) return;
    this.commentsService.deleteComment(this.article.slug, commentId).subscribe({
      next: () => this.comments = this.comments.filter(c => c.id !== commentId),
      error: (err) => console.error('Error deleting comment:', err)
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
