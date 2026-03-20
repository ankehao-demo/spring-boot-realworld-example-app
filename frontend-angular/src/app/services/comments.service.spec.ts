import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommentsService } from './comments.service';
import { environment } from '../../environments/environment';

describe('CommentsService', () => {
  let service: CommentsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(CommentsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get comments for an article', () => {
    const mockComments = [
      { id: '1', createdAt: '', updatedAt: '', body: 'Test comment', author: { username: 'user1', bio: null, image: null, following: false } }
    ];
    service.getComments('test-slug').subscribe(comments => {
      expect(comments.length).toBe(1);
      expect(comments[0].body).toBe('Test comment');
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test-slug/comments`);
    expect(req.request.method).toBe('GET');
    req.flush({ comments: mockComments });
  });

  it('should add a comment', () => {
    const mockComment = { id: '2', createdAt: '', updatedAt: '', body: 'New comment', author: { username: 'user1', bio: null, image: null, following: false } };
    service.addComment('test-slug', 'New comment').subscribe(comment => {
      expect(comment.body).toBe('New comment');
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test-slug/comments`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ comment: { body: 'New comment' } });
    req.flush({ comment: mockComment });
  });

  it('should delete a comment', () => {
    service.deleteComment('test-slug', '1').subscribe();
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/articles/test-slug/comments/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
