import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AuthService} from '../services/AuthService';
import {catchError, Observable, throwError} from 'rxjs';
import {EventSearchRequest} from './model/EventSearchRequest';
import {EventDTO} from './model/EventDTO';

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
}
