import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HomeComponent } from './home.component';
import { ArticlesService } from '../../services/articles.service';
import { TagsService } from '../../services/tags.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let articlesService: jasmine.SpyObj<ArticlesService>;
  let tagsService: jasmine.SpyObj<TagsService>;

  beforeEach(async () => {
    localStorage.clear();
    const articlesSpy = jasmine.createSpyObj('ArticlesService', ['getArticles', 'getFeed']);
    const tagsSpy = jasmine.createSpyObj('TagsService', ['getTags']);

    articlesSpy.getArticles.and.returnValue(of({ articles: [], articlesCount: 0 }));
    articlesSpy.getFeed.and.returnValue(of({ articles: [], articlesCount: 0 }));
    tagsSpy.getTags.and.returnValue(of(['angular', 'react']));

    await TestBed.configureTestingModule({
      imports: [HomeComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ArticlesService, useValue: articlesSpy },
        { provide: TagsService, useValue: tagsSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    articlesService = TestBed.inject(ArticlesService) as jasmine.SpyObj<ArticlesService>;
    tagsService = TestBed.inject(TagsService) as jasmine.SpyObj<TagsService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tags on init', () => {
    expect(tagsService.getTags).toHaveBeenCalled();
    expect(component.tags).toEqual(['angular', 'react']);
  });

  it('should load articles on init', () => {
    expect(articlesService.getArticles).toHaveBeenCalled();
  });

  it('should default to global tab', () => {
    expect(component.activeTab).toBe('global');
  });

  it('should switch to feed tab', () => {
    component.setActiveTab('feed');
    expect(component.activeTab).toBe('feed');
  });

  it('should clear selected tag when switching to global tab', () => {
    component.selectedTag = 'angular';
    component.setActiveTab('global');
    expect(component.selectedTag).toBe('');
  });

  it('should toggle tag selection', () => {
    component.onTagSelect('angular');
    expect(component.selectedTag).toBe('angular');
    component.onTagSelect('angular');
    expect(component.selectedTag).toBe('');
  });

  it('should update article in list', () => {
    const original = { slug: 'test', title: 'Test', description: '', body: '', tagList: [], createdAt: '', updatedAt: '', favorited: false, favoritesCount: 0, author: { username: 'test', bio: null, image: null, following: false } };
    const updated = { ...original, favorited: true, favoritesCount: 1 };
    component.articles = [original];
    component.onArticleUpdate(updated);
    expect(component.articles[0].favorited).toBeTrue();
  });
});
