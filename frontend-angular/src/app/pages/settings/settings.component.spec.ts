import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { SettingsComponent } from './settings.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    const authSpy = jasmine.createSpyObj('AuthService', ['updateUser', 'logout'], {
      currentUser: { email: 'test@test.com', username: 'testuser', bio: 'Bio', image: 'img.jpg' }
    });

    await TestBed.configureTestingModule({
      imports: [SettingsComponent, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should pre-populate form with user data', () => {
    expect(component.email).toBe('test@test.com');
    expect(component.username).toBe('testuser');
    expect(component.bio).toBe('Bio');
    expect(component.image).toBe('img.jpg');
  });

  it('should call updateUser on submit', () => {
    authService.updateUser.and.returnValue(of({ email: 'test@test.com', username: 'testuser', bio: 'Bio', image: 'img.jpg', token: 'token' }));
    spyOn(router, 'navigate');
    component.onSubmit();
    expect(authService.updateUser).toHaveBeenCalled();
  });

  it('should call logout and navigate', () => {
    spyOn(router, 'navigate');
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
