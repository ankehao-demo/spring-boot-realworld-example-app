import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ArticleViewComponent } from './pages/article-view/article-view.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ArticleEditorComponent } from './pages/article-editor/article-editor.component';
import { SettingsComponent } from './pages/settings/settings.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'article/:slug', component: ArticleViewComponent },
  { path: 'profile/:username', component: ProfileComponent },
  { path: 'editor', component: ArticleEditorComponent, canActivate: [authGuard] },
  { path: 'editor/:slug', component: ArticleEditorComponent, canActivate: [authGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
