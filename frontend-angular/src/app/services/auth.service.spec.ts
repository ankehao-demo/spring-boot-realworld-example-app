import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return null for currentUser initially', () => {
    expect(service.currentUser).toBeNull();
  });

  it('should not be authenticated initially', () => {
    expect(service.isAuthenticated).toBeFalse();
  });

  it('should return null token when not logged in', () => {
    expect(service.getToken()).toBeNull();
  });

  describe('login', () => {
    it('should send login request and store token', () => {
      const mockUser = {
        email: 'test@test.com',
        username: 'testuser',
        bio: null,
        image: null,
        token: 'test-jwt-token'
      };

      service.login('test@test.com', 'password').subscribe(user => {
        expect(user.email).toBe('test@test.com');
        expect(user.token).toBe('test-jwt-token');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/users/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ user: { email: 'test@test.com', password: 'password' } });
      req.flush({ user: mockUser });

      expect(localStorage.getItem('jwtToken')).toBe('test-jwt-token');
      expect(service.currentUser?.email).toBe('test@test.com');
    });
  });

  describe('register', () => {
    it('should send register request and store token', () => {
      const mockUser = {
        email: 'new@test.com',
        username: 'newuser',
        bio: null,
        image: null,
        token: 'new-jwt-token'
      };

      service.register('newuser', 'new@test.com', 'password').subscribe(user => {
        expect(user.username).toBe('newuser');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/users`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ user: { username: 'newuser', email: 'new@test.com', password: 'password' } });
      req.flush({ user: mockUser });

      expect(localStorage.getItem('jwtToken')).toBe('new-jwt-token');
    });
  });

  describe('updateUser', () => {
    it('should send update request', () => {
      const mockUser = {
        email: 'updated@test.com',
        username: 'testuser',
        bio: 'Updated bio',
        image: null,
        token: 'updated-token'
      };

      service.updateUser({ email: 'updated@test.com', bio: 'Updated bio' }).subscribe(user => {
        expect(user.email).toBe('updated@test.com');
      });

      const req = httpMock.expectOne(`${environment.apiBaseUrl}/user`);
      expect(req.request.method).toBe('PUT');
      req.flush({ user: mockUser });
    });
  });

  describe('logout', () => {
    it('should remove token and clear user', () => {
      localStorage.setItem('jwtToken', 'test-token');
      service.logout();
      expect(localStorage.getItem('jwtToken')).toBeNull();
      expect(service.currentUser).toBeNull();
    });
  });
});
