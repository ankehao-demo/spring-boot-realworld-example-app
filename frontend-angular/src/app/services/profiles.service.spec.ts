import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProfilesService } from './profiles.service';
import { environment } from '../../environments/environment';

describe('ProfilesService', () => {
  let service: ProfilesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ProfilesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a user profile', () => {
    const mockProfile = { username: 'testuser', bio: 'Bio', image: null, following: false };
    service.getProfile('testuser').subscribe(profile => {
      expect(profile.username).toBe('testuser');
      expect(profile.following).toBeFalse();
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/profiles/testuser`);
    expect(req.request.method).toBe('GET');
    req.flush({ profile: mockProfile });
  });

  it('should follow a user', () => {
    const mockProfile = { username: 'testuser', bio: 'Bio', image: null, following: true };
    service.followUser('testuser').subscribe(profile => {
      expect(profile.following).toBeTrue();
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/profiles/testuser/follow`);
    expect(req.request.method).toBe('POST');
    req.flush({ profile: mockProfile });
  });

  it('should unfollow a user', () => {
    const mockProfile = { username: 'testuser', bio: 'Bio', image: null, following: false };
    service.unfollowUser('testuser').subscribe(profile => {
      expect(profile.following).toBeFalse();
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/profiles/testuser/follow`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ profile: mockProfile });
  });
});
