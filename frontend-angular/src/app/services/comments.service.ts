import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Comment } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CommentsService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getComments(slug: string): Observable<Comment[]> {
    return this.http.get<{ comments: Comment[] }>(`${this.apiUrl}/articles/${slug}/comments`).pipe(
      map(response => response.comments)
    );
  }

  addComment(slug: string, body: string): Observable<Comment> {
    return this.http.post<{ comment: Comment }>(`${this.apiUrl}/articles/${slug}/comments`, {
      comment: { body }
    }).pipe(
      map(response => response.comment)
    );
  }

  deleteComment(slug: string, commentId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/articles/${slug}/comments/${commentId}`);
  }
}
