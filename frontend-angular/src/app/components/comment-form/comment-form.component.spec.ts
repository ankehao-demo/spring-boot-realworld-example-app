import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CommentFormComponent } from './comment-form.component';

describe('CommentFormComponent', () => {
  let component: CommentFormComponent;
  let fixture: ComponentFixture<CommentFormComponent>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [CommentFormComponent, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CommentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit comment on submit', () => {
    spyOn(component.submitComment, 'emit');
    component.body = 'Test comment';
    component.onSubmit();
    expect(component.submitComment.emit).toHaveBeenCalledWith('Test comment');
    expect(component.body).toBe('');
  });

  it('should not emit empty comment', () => {
    spyOn(component.submitComment, 'emit');
    component.body = '   ';
    component.onSubmit();
    expect(component.submitComment.emit).not.toHaveBeenCalled();
  });
});
