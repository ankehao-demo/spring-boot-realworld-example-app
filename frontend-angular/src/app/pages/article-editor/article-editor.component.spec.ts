import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { ArticleEditorComponent } from './article-editor.component';
import { ArticlesService } from '../../services/articles.service';
import { of } from 'rxjs';

describe('ArticleEditorComponent', () => {
  let component: ArticleEditorComponent;
  let fixture: ComponentFixture<ArticleEditorComponent>;

  beforeEach(async () => {
    localStorage.clear();
    const articlesSpy = jasmine.createSpyObj('ArticlesService', ['getArticle', 'createArticle', 'updateArticle']);
    articlesSpy.createArticle.and.returnValue(of({ slug: 'new-article', title: 'New', description: '', body: '', tagList: [], createdAt: '', updatedAt: '', favorited: false, favoritesCount: 0, author: { username: 'test', bio: null, image: null, following: false } }));

    await TestBed.configureTestingModule({
      imports: [ArticleEditorComponent, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        { provide: ArticlesService, useValue: articlesSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start in create mode', () => {
    expect(component.isEditing).toBeFalse();
  });

  it('should have empty fields initially', () => {
    expect(component.title).toBe('');
    expect(component.description).toBe('');
    expect(component.body).toBe('');
    expect(component.tagList).toEqual([]);
  });

  it('should add a tag on Enter key press', () => {
    component.tagInput = 'newtag';
    const event = new KeyboardEvent('keypress', { key: 'Enter' });
    spyOn(event, 'preventDefault');
    component.onTagKeyPress(event);
    expect(component.tagList).toContain('newtag');
    expect(component.tagInput).toBe('');
  });

  it('should not add empty tag', () => {
    component.tagInput = '   ';
    const event = new KeyboardEvent('keypress', { key: 'Enter' });
    spyOn(event, 'preventDefault');
    component.onTagKeyPress(event);
    expect(component.tagList.length).toBe(0);
  });

  it('should not add duplicate tag', () => {
    component.tagList = ['existing'];
    component.tagInput = 'existing';
    const event = new KeyboardEvent('keypress', { key: 'Enter' });
    spyOn(event, 'preventDefault');
    component.onTagKeyPress(event);
    expect(component.tagList.length).toBe(1);
  });

  it('should remove a tag', () => {
    component.tagList = ['tag1', 'tag2'];
    component.removeTag('tag1');
    expect(component.tagList).toEqual(['tag2']);
  });
});
