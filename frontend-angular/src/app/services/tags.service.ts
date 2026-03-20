import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TagsService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getTags(): Observable<string[]> {
    return this.http.get<{ tags: string[] }>(`${this.apiUrl}/tags`).pipe(
      map(response => response.tags)
    );
  }
}
