import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    const authSpy = jasmine.createSpyObj('AuthService', ['register'], { currentUser: null, isAuthenticated: false });

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty fields initially', () => {
    expect(component.username).toBe('');
    expect(component.email).toBe('');
    expect(component.password).toBe('');
  });

  it('should call register on submit', () => {
    authService.register.and.returnValue(of({ email: 'new@test.com', username: 'newuser', bio: null, image: null, token: 'token' }));
    spyOn(router, 'navigate');
    component.username = 'newuser';
    component.email = 'new@test.com';
    component.password = 'password';
    component.onSubmit();
    expect(authService.register).toHaveBeenCalledWith('newuser', 'new@test.com', 'password');
  });

  it('should navigate to home on successful registration', () => {
    authService.register.and.returnValue(of({ email: 'new@test.com', username: 'newuser', bio: null, image: null, token: 'token' }));
    spyOn(router, 'navigate');
    component.username = 'newuser';
    component.email = 'new@test.com';
    component.password = 'password';
    component.onSubmit();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should display error on registration failure', () => {
    authService.register.and.returnValue(throwError(() => ({ error: { errors: { email: ['has already been taken'] } } })));
    component.username = 'newuser';
    component.email = 'existing@test.com';
    component.password = 'password';
    component.onSubmit();
    expect(component.error).toContain('has already been taken');
    expect(component.isLoading).toBeFalse();
  });
});
