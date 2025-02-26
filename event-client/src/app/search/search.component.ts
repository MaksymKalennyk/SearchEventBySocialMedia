import { Component } from '@angular/core';
import {HttpClient} from '@angular/common/http';


interface Event {
  id: number;
  chatId: number;
  messageId: number;
  rawText: string;
  eventType: string;
  eventDate: string;
  price: string;
  city: string;
  url: string;
  createdAt: string;
}

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

  constructor(private http: HttpClient) {}

  onSubmit(): void {
    this.error = '';
    this.http.post<Event[]>('http://localhost:8080/api/search', this.query)
      .subscribe({
        next: (data) => {
          this.results = data;
        },
        error: (err) => {
          console.error(err);
          this.error = 'Помилка при виконанні пошуку';
        }
      });
  }
}
