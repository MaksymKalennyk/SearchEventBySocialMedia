import {Component, OnInit} from '@angular/core';
import {SearchService} from '../search/SearchService';
import {EventDTO} from '../search/model/EventDTO';

@Component({
  selector: 'app-recommendation-list',
  standalone: false,
  templateUrl: './recommendation-list.component.html',
  styleUrl: './recommendation-list.component.css'
})
export class RecommendationListComponent implements OnInit {
  recommendedEvents: EventDTO[] = [];
  error: string = '';

  constructor(private searchService: SearchService) {}

  ngOnInit(): void {
    this.searchService.getTopEvents().subscribe({
      next: (data) => {
        this.recommendedEvents = data;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Не вдалося завантажити рекомендації';
      }
    });
  }

  /**
   * Метод, що відправляє trackEvent, а потім відкриває посилання
   */
  trackAndOpen(event: EventDTO, e: MouseEvent): void {
    e.preventDefault();
    this.searchService.trackEvent(event.id).subscribe({
      next: () => {
        if (event.url) {
          window.open(event.url, '_blank');
        }
      },
      error: (err) => {
        console.error('Помилка trackEvent:', err);
        if (event.url) {
          window.open(event.url, '_blank');
        }
      }
    });
  }
}
