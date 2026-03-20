import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CommentListComponent } from './comment-list.component';
import { Comment } from '../../models';

describe('CommentListComponent', () => {
  let component: CommentListComponent;
  let fixture: ComponentFixture<CommentListComponent>;

  const mockComments: Comment[] = [
    { id: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z', body: 'Comment 1', author: { username: 'user1', bio: null, image: null, following: false } },
    { id: '2', createdAt: '2024-01-02T00:00:00Z', updatedAt: '2024-01-02T00:00:00Z', body: 'Comment 2', author: { username: 'user2', bio: null, image: null, following: false } }
  ];

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [CommentListComponent, RouterTestingModule, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CommentListComponent);
    component = fixture.componentInstance;
    component.comments = mockComments;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all comments', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Comment 1');
    expect(compiled.textContent).toContain('Comment 2');
  });

  it('should emit delete event', () => {
    spyOn(component.deleteComment, 'emit');
    component.onDelete('1');
    expect(component.deleteComment.emit).toHaveBeenCalledWith('1');
  });
});
