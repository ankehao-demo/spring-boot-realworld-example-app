import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ArticleViewComponent } from './article-view.component';
import { ArticlesService } from '../../services/articles.service';
import { CommentsService } from '../../services/comments.service';
import { ProfilesService } from '../../services/profiles.service';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('ArticleViewComponent', () => {
  let component: ArticleViewComponent;
  let fixture: ComponentFixture<ArticleViewComponent>;

  const mockArticle = {
    slug: 'test-article',
    title: 'Test Article',
    description: 'Description',
    body: 'Body content',
    tagList: ['test'],
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    favorited: false,
    favoritesCount: 3,
    author: { username: 'author', bio: 'Bio', image: null, following: false }
  };

  beforeEach(async () => {
    localStorage.clear();
    const articlesSpy = jasmine.createSpyObj('ArticlesService', ['getArticle', 'deleteArticle', 'favoriteArticle', 'unfavoriteArticle']);
    const commentsSpy = jasmine.createSpyObj('CommentsService', ['getComments', 'addComment', 'deleteComment']);
    const profilesSpy = jasmine.createSpyObj('ProfilesService', ['followUser', 'unfollowUser']);

    articlesSpy.getArticle.and.returnValue(of(mockArticle));
    commentsSpy.getComments.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ArticleViewComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ArticlesService, useValue: articlesSpy },
        { provide: CommentsService, useValue: commentsSpy },
        { provide: ProfilesService, useValue: profilesSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: (key: string) => key === 'slug' ? 'test-article' : null } }, params: of({ slug: 'test-article' }) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load article on init', () => {
    expect(component.article).toBeTruthy();
    expect(component.article?.title).toBe('Test Article');
  });

  it('should load comments on init', () => {
    expect(component.comments).toEqual([]);
  });
});
