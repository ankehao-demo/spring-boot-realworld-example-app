import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ArticlesService } from './articles.service';
import { environment } from '../../environments/environment';

describe('ArticlesService', () => {
  let service: ArticlesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ArticlesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getArticles', () => {
    it('should fetch articles without params', () => {
      const mockResponse = { articles: [], articlesCount: 0 };
      service.getArticles().subscribe(data => {
        expect(data.articles).toEqual([]);
        expect(data.articlesCount).toBe(0);
      });
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should fetch articles with tag param', () => {
      service.getArticles({ tag: 'angular' }).subscribe();
      const req = httpMock.expectOne(r => r.url === `${environment.apiBaseUrl}/articles` && r.params.get('tag') === 'angular');
      expect(req.request.method).toBe('GET');
      req.flush({ articles: [], articlesCount: 0 });
    });

    it('should fetch articles with author param', () => {
      service.getArticles({ author: 'testuser' }).subscribe();
      const req = httpMock.expectOne(r => r.url === `${environment.apiBaseUrl}/articles` && r.params.get('author') === 'testuser');
      expect(req.request.method).toBe('GET');
      req.flush({ articles: [], articlesCount: 0 });
    });
  });

  describe('getFeed', () => {
    it('should fetch user feed', () => {
      service.getFeed().subscribe();
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/feed`);
      expect(req.request.method).toBe('GET');
      req.flush({ articles: [], articlesCount: 0 });
    });
  });

  describe('getArticle', () => {
    it('should fetch a single article by slug', () => {
      const mockArticle = { slug: 'test-article', title: 'Test', description: '', body: '', tagList: [], createdAt: '', updatedAt: '', favorited: false, favoritesCount: 0, author: { username: 'test', bio: null, image: null, following: false } };
      service.getArticle('test-article').subscribe(article => {
        expect(article.slug).toBe('test-article');
      });
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test-article`);
      expect(req.request.method).toBe('GET');
      req.flush({ article: mockArticle });
    });
  });

  describe('createArticle', () => {
    it('should create an article', () => {
      const newArticle = { title: 'New', description: 'Desc', body: 'Body', tagList: ['test'] };
      const mockArticle = { slug: 'new', title: 'New', description: 'Desc', body: 'Body', tagList: ['test'], createdAt: '', updatedAt: '', favorited: false, favoritesCount: 0, author: { username: 'test', bio: null, image: null, following: false } };
      service.createArticle(newArticle).subscribe(article => {
        expect(article.title).toBe('New');
      });
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ article: newArticle });
      req.flush({ article: mockArticle });
    });
  });

  describe('deleteArticle', () => {
    it('should delete an article', () => {
      service.deleteArticle('test-slug').subscribe();
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test-slug`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('favoriteArticle', () => {
    it('should favorite an article', () => {
      const mockArticle = { slug: 'test', title: 'Test', description: '', body: '', tagList: [], createdAt: '', updatedAt: '', favorited: true, favoritesCount: 1, author: { username: 'test', bio: null, image: null, following: false } };
      service.favoriteArticle('test').subscribe(article => {
        expect(article.favorited).toBeTrue();
      });
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test/favorite`);
      expect(req.request.method).toBe('POST');
      req.flush({ article: mockArticle });
    });
  });

  describe('unfavoriteArticle', () => {
    it('should unfavorite an article', () => {
      const mockArticle = { slug: 'test', title: 'Test', description: '', body: '', tagList: [], createdAt: '', updatedAt: '', favorited: false, favoritesCount: 0, author: { username: 'test', bio: null, image: null, following: false } };
      service.unfavoriteArticle('test').subscribe(article => {
        expect(article.favorited).toBeFalse();
      });
      const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test/favorite`);
      expect(req.request.method).toBe('DELETE');
      req.flush({ article: mockArticle });
    });
  });
});
