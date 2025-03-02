import { Component } from '@angular/core';
import { UserLogin } from './UserLogin';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../services/AuthService';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  user: UserLogin = { username: '', password: '' };
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]],
    });
  }

  submitForm() {
    this.authService.signIn(this.user).subscribe({
      next: (response) => {
        this.authService.setToken(response.token);
        this.router.navigate(["/search"])
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  registration() {
    this.router.navigate(["/registration"]);
  }
}
