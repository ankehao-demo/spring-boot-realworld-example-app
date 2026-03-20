import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ProfileComponent } from './profile.component';
import { ProfilesService } from '../../services/profiles.service';
import { ArticlesService } from '../../services/articles.service';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  const mockProfile = { username: 'testuser', bio: 'Test bio', image: null, following: false };

  beforeEach(async () => {
    localStorage.clear();
    const profilesSpy = jasmine.createSpyObj('ProfilesService', ['getProfile', 'followUser', 'unfollowUser']);
    const articlesSpy = jasmine.createSpyObj('ArticlesService', ['getArticles']);

    profilesSpy.getProfile.and.returnValue(of(mockProfile));
    articlesSpy.getArticles.and.returnValue(of({ articles: [], articlesCount: 0 }));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ProfilesService, useValue: profilesSpy },
        { provide: ArticlesService, useValue: articlesSpy },
        { provide: ActivatedRoute, useValue: { paramMap: of({ get: (key: string) => key === 'username' ? 'testuser' : null }), params: of({ username: 'testuser' }) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile on init', () => {
    expect(component.profile).toBeTruthy();
    expect(component.profile?.username).toBe('testuser');
  });

  it('should default to articles tab', () => {
    expect(component.activeTab).toBe('articles');
  });

  it('should switch tabs', () => {
    component.setActiveTab('favorites');
    expect(component.activeTab).toBe('favorites');
  });
});
