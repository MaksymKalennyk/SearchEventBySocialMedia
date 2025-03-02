import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {JwtAuthenticationResponse} from './JwtAuthenticationResponse';
import {UserRegistration} from '../registration/UserRegistartion';
import {UserLogin} from '../login/UserLogin';

@Injectable({
  providedIn: 'root'
})
export class AuthService{
  private apiUrl = 'http://localhost:8080/auth';
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) { }

  signUp(request: UserRegistration): Observable<JwtAuthenticationResponse> {
    return this.http.post<JwtAuthenticationResponse>(`${this.apiUrl}/sign-up`, request);
  }

  signIn(request: UserLogin): Observable<JwtAuthenticationResponse> {
    return this.http.post<JwtAuthenticationResponse>(`${this.apiUrl}/sign-in`, request);
  }

  setToken(token: string) {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }
}
