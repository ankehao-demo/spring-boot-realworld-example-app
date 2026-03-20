import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ArticleCardComponent } from './article-card.component';
import { Article } from '../../models';

describe('ArticleCardComponent', () => {
  let component: ArticleCardComponent;
  let fixture: ComponentFixture<ArticleCardComponent>;

  const mockArticle: Article = {
    slug: 'test-article',
    title: 'Test Article',
    description: 'Test description',
    body: 'Test body',
    tagList: ['test', 'angular'],
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    favorited: false,
    favoritesCount: 5,
    author: { username: 'testuser', bio: null, image: null, following: false }
  };

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [ArticleCardComponent, RouterTestingModule, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCardComponent);
    component = fixture.componentInstance;
    component.article = mockArticle;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display article title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Test Article');
  });

  it('should display article description', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Test description');
  });

  it('should display author username', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('testuser');
  });

  it('should have favoritesCount in article data', () => {
    expect(component.article.favoritesCount).toBe(5);
  });

  it('should format date correctly', () => {
    const formatted = component.formatDate('2024-01-01T00:00:00Z');
    expect(formatted).toContain('January');
    expect(formatted).toContain('2024');
  });

  it('should return default image', () => {
    expect(component.defaultImage).toContain('smiley-cyrus');
  });
});
