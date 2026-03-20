import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    const authSpy = jasmine.createSpyObj('AuthService', ['login'], { currentUser: null, isAuthenticated: false });

    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty fields initially', () => {
    expect(component.email).toBe('');
    expect(component.password).toBe('');
    expect(component.error).toBe('');
  });

  it('should call login on submit', () => {
    authService.login.and.returnValue(of({ email: 'test@test.com', username: 'test', bio: null, image: null, token: 'token' }));
    spyOn(router, 'navigate');
    component.email = 'test@test.com';
    component.password = 'password';
    component.onSubmit();
    expect(authService.login).toHaveBeenCalledWith('test@test.com', 'password');
  });

  it('should navigate to home on successful login', () => {
    authService.login.and.returnValue(of({ email: 'test@test.com', username: 'test', bio: null, image: null, token: 'token' }));
    spyOn(router, 'navigate');
    component.email = 'test@test.com';
    component.password = 'password';
    component.onSubmit();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should display error on login failure', () => {
    authService.login.and.returnValue(throwError(() => ({ error: { errors: { 'email or password': ['is invalid'] } } })));
    component.email = 'test@test.com';
    component.password = 'wrong';
    component.onSubmit();
    expect(component.error).toBe('is invalid');
    expect(component.isLoading).toBeFalse();
  });
});
