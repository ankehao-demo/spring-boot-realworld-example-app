import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Profile } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProfilesService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getProfile(username: string): Observable<Profile> {
    return this.http.get<{ profile: Profile }>(`${this.apiUrl}/profiles/${username}`).pipe(
      map(response => response.profile)
    );
  }

  followUser(username: string): Observable<Profile> {
    return this.http.post<{ profile: Profile }>(`${this.apiUrl}/profiles/${username}/follow`, {}).pipe(
      map(response => response.profile)
    );
  }

  unfollowUser(username: string): Observable<Profile> {
    return this.http.delete<{ profile: Profile }>(`${this.apiUrl}/profiles/${username}/follow`).pipe(
      map(response => response.profile)
    );
  }
}
