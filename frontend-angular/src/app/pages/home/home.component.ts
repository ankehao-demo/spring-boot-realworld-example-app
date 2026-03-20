import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';
import { TagListComponent } from '../../components/tag-list/tag-list.component';
import { ArticlesService } from '../../services/articles.service';
import { TagsService } from '../../services/tags.service';
import { AuthService } from '../../services/auth.service';
import { Article } from '../../models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ArticleCardComponent, TagListComponent],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  articles: Article[] = [];
  tags: string[] = [];
  selectedTag = '';
  activeTab: 'global' | 'feed' = 'global';
  isLoading = true;

  constructor(
    private articlesService: ArticlesService,
    private tagsService: TagsService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadTags();
    this.loadArticles();
  }

  loadTags(): void {
    this.tagsService.getTags().subscribe({
      next: (tags) => this.tags = tags,
      error: (err) => console.error('Error loading tags:', err)
    });
  }

  loadArticles(): void {
    this.isLoading = true;

    if (this.activeTab === 'feed' && this.authService.currentUser) {
      this.articlesService.getFeed().subscribe({
        next: (data) => {
          this.articles = data.articles;
          this.isLoading = false;
        },
        error: () => this.isLoading = false
      });
    } else {
      const params = this.selectedTag ? { tag: this.selectedTag } : {};
      this.articlesService.getArticles(params).subscribe({
        next: (data) => {
          this.articles = data.articles;
          this.isLoading = false;
        },
        error: () => this.isLoading = false
      });
    }
  }

  setActiveTab(tab: 'global' | 'feed'): void {
    this.activeTab = tab;
    if (tab === 'global') {
      this.selectedTag = '';
    }
    this.loadArticles();
  }

  onTagSelect(tag: string): void {
    this.selectedTag = this.selectedTag === tag ? '' : tag;
    this.activeTab = 'global';
    this.loadArticles();
  }

  onArticleUpdate(updatedArticle: Article): void {
    this.articles = this.articles.map(article =>
      article.slug === updatedArticle.slug ? updatedArticle : article
    );
  }
}
