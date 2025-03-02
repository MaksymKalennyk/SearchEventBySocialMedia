import { Component } from '@angular/core';
import {SearchService} from './SearchService';
import { Event } from './model/Event';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent {
  query: string = '';
  results: Event[] = [];
  error: string = '';

  constructor(private searchService: SearchService) {}

  onSubmit(): void {
    this.error = '';
    this.searchService.searchEvents(this.query).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => {
        console.error(err);
        this.error = err.message;
      }
    });
  }
}
