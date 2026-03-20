import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TagsService } from './tags.service';
import { environment } from '../../environments/environment';

describe('TagsService', () => {
  let service: TagsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(TagsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get tags', () => {
    const mockTags = ['angular', 'react', 'vue'];
    service.getTags().subscribe(tags => {
      expect(tags.length).toBe(3);
      expect(tags).toContain('angular');
    });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/tags`);
    expect(req.request.method).toBe('GET');
    req.flush({ tags: mockTags });
  });
});
