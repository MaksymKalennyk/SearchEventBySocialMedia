import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AuthService} from '../services/AuthService';
import {catchError, Observable, throwError} from 'rxjs';
import {EventSearchRequest} from './model/EventSearchRequest';
import {EventDTO} from './model/EventDTO';
import {EventTopDTO} from '../recommendation-list/model/EventTopDTO';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient,
              private authService: AuthService) { }

  searchEvents(req: EventSearchRequest): Observable<EventDTO[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    return this.http.post<EventDTO[]>(`${this.apiUrl}/search`, req, { headers })
      .pipe(
        catchError(error => {
          console.error('Помилка при пошуку івентів:', error);
          return throwError(() => new Error('Помилка при виконанні пошуку'));
        })
      );
  }

  trackEvent(eventId: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    return this.http.post<void>(`${this.apiUrl}/events/track/${eventId}`, null, { headers })
      .pipe(
        catchError(error => {
          console.error('Помилка при трекінгу івенту:', error);
          return throwError(() => new Error('Помилка при трекінгу івенту'));
        })
      );
  }

  getTopEvents(): Observable<EventTopDTO[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    return this.http.get<EventTopDTO[]>(`${this.apiUrl}/events/top`, { headers })
      .pipe(
        catchError(error => {
          console.error('Помилка при отриманні рекомендацій:', error);
          return throwError(() => new Error('Помилка при отриманні рекомендацій'));
        })
      );
  }
}
