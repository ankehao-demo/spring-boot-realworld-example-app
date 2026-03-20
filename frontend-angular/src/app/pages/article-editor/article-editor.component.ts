import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { NewArticle } from '../../models';

@Component({
  selector: 'app-article-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './article-editor.component.html'
})
export class ArticleEditorComponent implements OnInit {
  title = '';
  description = '';
  body = '';
  tagInput = '';
  tagList: string[] = [];
  isLoading = false;
  error = '';
  slug: string | null = null;

  get isEditing(): boolean {
    return !!this.slug;
  }

  constructor(
    private articlesService: ArticlesService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.slug = this.route.snapshot.paramMap.get('slug');
    if (this.slug) {
      this.loadArticle(this.slug);
    }
  }

  loadArticle(slug: string): void {
    this.articlesService.getArticle(slug).subscribe({
      next: (article) => {
        this.title = article.title;
        this.description = article.description;
        this.body = article.body;
        this.tagList = [...article.tagList];
      },
      error: () => {
        this.error = 'Failed to load article';
      }
    });
  }

  onSubmit(): void {
    this.error = '';
    this.isLoading = true;

    const articleData: NewArticle = {
      title: this.title,
      description: this.description,
      body: this.body,
      tagList: this.tagList
    };

    const action = this.isEditing && this.slug
      ? this.articlesService.updateArticle(this.slug, articleData)
      : this.articlesService.createArticle(articleData);

    action.subscribe({
      next: (article) => {
        this.router.navigate(['/article', article.slug]);
      },
      error: (err) => {
        const errors = err.error?.errors;
        if (errors) {
          this.error = Object.entries(errors)
            .map(([field, messages]: [string, any]) => `${field}: ${(messages as string[]).join(', ')}`)
            .join('; ');
        } else {
          this.error = 'Failed to save article';
        }
        this.isLoading = false;
      }
    });
  }

  onTagKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.tagInput.trim()) {
      event.preventDefault();
      const newTag = this.tagInput.trim();
      if (!this.tagList.includes(newTag)) {
        this.tagList.push(newTag);
      }
      this.tagInput = '';
    }
  }

  removeTag(tag: string): void {
    this.tagList = this.tagList.filter(t => t !== tag);
  }
}
