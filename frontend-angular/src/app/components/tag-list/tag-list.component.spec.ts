import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TagListComponent } from './tag-list.component';

describe('TagListComponent', () => {
  let component: TagListComponent;
  let fixture: ComponentFixture<TagListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TagListComponent);
    component = fixture.componentInstance;
    component.tags = ['angular', 'react', 'vue'];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all tags', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('angular');
    expect(compiled.textContent).toContain('react');
    expect(compiled.textContent).toContain('vue');
  });

  it('should emit tag selection', () => {
    spyOn(component.tagSelect, 'emit');
    component.onTagSelect('angular');
    expect(component.tagSelect.emit).toHaveBeenCalledWith('angular');
  });
});
