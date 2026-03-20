import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Article, ArticlesResponse, ArticlesParams, NewArticle } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getArticles(params: ArticlesParams = {}): Observable<ArticlesResponse> {
    let httpParams = new HttpParams();
    if (params.tag) httpParams = httpParams.set('tag', params.tag);
    if (params.author) httpParams = httpParams.set('author', params.author);
    if (params.favorited) httpParams = httpParams.set('favorited', params.favorited);
    if (params.limit != null) httpParams = httpParams.set('limit', params.limit.toString());
    if (params.offset != null) httpParams = httpParams.set('offset', params.offset.toString());

    return this.http.get<ArticlesResponse>(`${this.apiUrl}/articles`, { params: httpParams });
  }

  getFeed(): Observable<ArticlesResponse> {
    return this.http.get<ArticlesResponse>(`${this.apiUrl}/articles/feed`);
  }

  getArticle(slug: string): Observable<Article> {
    return this.http.get<{ article: Article }>(`${this.apiUrl}/articles/${slug}`).pipe(
      map(response => response.article)
    );
  }

  createArticle(article: NewArticle): Observable<Article> {
    return this.http.post<{ article: Article }>(`${this.apiUrl}/articles`, {
      article
    }).pipe(
      map(response => response.article)
    );
  }

  updateArticle(slug: string, article: Partial<NewArticle>): Observable<Article> {
    return this.http.put<{ article: Article }>(`${this.apiUrl}/articles/${slug}`, {
      article
    }).pipe(
      map(response => response.article)
    );
  }

  deleteArticle(slug: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/articles/${slug}`);
  }

  favoriteArticle(slug: string): Observable<Article> {
    return this.http.post<{ article: Article }>(`${this.apiUrl}/articles/${slug}/favorite`, {}).pipe(
      map(response => response.article)
    );
  }

  unfavoriteArticle(slug: string): Observable<Article> {
    return this.http.delete<{ article: Article }>(`${this.apiUrl}/articles/${slug}/favorite`).pipe(
      map(response => response.article)
    );
  }
}
