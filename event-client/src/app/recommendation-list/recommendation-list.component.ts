import {Component, OnInit} from '@angular/core';
import {SearchService} from '../search/SearchService';
import {EventTopDTO} from './model/EventTopDTO';

@Component({
  selector: 'app-recommendation-list',
  standalone: false,
  templateUrl: './recommendation-list.component.html',
  styleUrl: './recommendation-list.component.css'
})
export class RecommendationListComponent implements OnInit {
  recommendedEvents: EventTopDTO[] = [];
  error: string = '';

  constructor(private searchService: SearchService) {}

  ngOnInit(): void {
    this.searchService.getTopEvents().subscribe({
      next: (data) => {
        this.recommendedEvents = data;
        console.log(data);
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
  trackAndOpen(event: EventTopDTO, e: MouseEvent): void {
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
