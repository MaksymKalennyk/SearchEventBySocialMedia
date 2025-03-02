import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AuthService} from '../services/AuthService';
import { Event } from './model/Event';
import {catchError, Observable, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient, private authService: AuthService) { }

  searchEvents(query: string): Observable<Event[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    return this.http.post<Event[]>(`${this.apiUrl}/search`, { query }, { headers }).pipe(
      catchError(error => {
        console.error('Error during event search:', error);
        return throwError(() => new Error('Помилка при виконанні пошуку'));
      })
    );
  }
}
