import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, UserWithToken, UpdateUser } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiBaseUrl;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(true);

  currentUser$ = this.currentUserSubject.asObservable();
  isLoading$ = this.isLoadingSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUser();
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  get isAuthenticated(): boolean {
    return !!this.currentUser && !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  private setToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  private removeToken(): void {
    localStorage.removeItem('jwtToken');
  }

  private loadUser(): void {
    const token = this.getToken();
    if (token) {
      this.http.get<{ user: UserWithToken }>(`${this.apiUrl}/user`).subscribe({
        next: (response) => {
          this.currentUserSubject.next(response.user);
          this.isLoadingSubject.next(false);
        },
        error: () => {
          this.removeToken();
          this.currentUserSubject.next(null);
          this.isLoadingSubject.next(false);
        }
      });
    } else {
      this.isLoadingSubject.next(false);
    }
  }

  login(email: string, password: string): Observable<UserWithToken> {
    return this.http.post<{ user: UserWithToken }>(`${this.apiUrl}/users/login`, {
      user: { email, password }
    }).pipe(
      map(response => response.user),
      tap(user => {
        this.setToken(user.token);
        this.currentUserSubject.next(user);
      })
    );
  }

  register(username: string, email: string, password: string): Observable<UserWithToken> {
    return this.http.post<{ user: UserWithToken }>(`${this.apiUrl}/users`, {
      user: { username, email, password }
    }).pipe(
      map(response => response.user),
      tap(user => {
        this.setToken(user.token);
        this.currentUserSubject.next(user);
      })
    );
  }

  updateUser(data: UpdateUser): Observable<UserWithToken> {
    return this.http.put<{ user: UserWithToken }>(`${this.apiUrl}/user`, {
      user: data
    }).pipe(
      map(response => response.user),
      tap(user => {
        if (user.token) {
          this.setToken(user.token);
        }
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    this.removeToken();
    this.currentUserSubject.next(null);
  }
}
