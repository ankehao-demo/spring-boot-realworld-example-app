import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tag-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tag-list.component.html'
})
export class TagListComponent {
  @Input() tags: string[] = [];
  @Input() selectedTag = '';
  @Output() tagSelect = new EventEmitter<string>();

  onTagSelect(tag: string): void {
    this.tagSelect.emit(tag);
  }
}
