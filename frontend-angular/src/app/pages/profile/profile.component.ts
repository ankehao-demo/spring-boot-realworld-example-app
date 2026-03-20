import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ProfilesService } from '../../services/profiles.service';
import { ArticlesService } from '../../services/articles.service';
import { AuthService } from '../../services/auth.service';
import { Profile, Article } from '../../models';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, ArticleCardComponent],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  profile: Profile | null = null;
  articles: Article[] = [];
  activeTab: 'articles' | 'favorites' = 'articles';
  isLoading = true;
  isFollowLoading = false;

  constructor(
    private profilesService: ProfilesService,
    private articlesService: ArticlesService,
    public authService: AuthService,
    private route: ActivatedRoute
  ) {}

  get defaultImage(): string {
    return 'https://static.productionready.io/images/smiley-cyrus.jpg';
  }

  get isOwnProfile(): boolean {
    return !!this.authService.currentUser && !!this.profile &&
      this.authService.currentUser.username === this.profile.username;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const username = params.get('username');
      if (username) {
        this.loadProfile(username);
        this.loadArticles(username);
      }
    });
  }

  loadProfile(username: string): void {
    this.profilesService.getProfile(username).subscribe({
      next: (profile) => this.profile = profile,
      error: (err) => console.error('Error loading profile:', err)
    });
  }

  loadArticles(username?: string): void {
    const user = username || this.profile?.username;
    if (!user) return;

    this.isLoading = true;
    const params = this.activeTab === 'articles'
      ? { author: user }
      : { favorited: user };

    this.articlesService.getArticles(params).subscribe({
      next: (data) => {
        this.articles = data.articles;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  setActiveTab(tab: 'articles' | 'favorites'): void {
    this.activeTab = tab;
    this.loadArticles();
  }

  handleFollow(): void {
    if (!this.profile || !this.authService.currentUser || this.isFollowLoading) return;

    this.isFollowLoading = true;
    const action = this.profile.following
      ? this.profilesService.unfollowUser(this.profile.username)
      : this.profilesService.followUser(this.profile.username);

    action.subscribe({
      next: (profile) => {
        this.profile = profile;
        this.isFollowLoading = false;
      },
      error: () => this.isFollowLoading = false
    });
  }

  onArticleUpdate(updatedArticle: Article): void {
    this.articles = this.articles.map(article =>
      article.slug === updatedArticle.slug ? updatedArticle : article
    );
  }
}
