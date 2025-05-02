import {Component, OnInit} from '@angular/core';
import {SearchService} from './SearchService';
import {EventDTO} from './model/EventDTO';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators} from '@angular/forms';

@Component({
  selector: 'app-search',
  standalone: false,
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  searchForm!: FormGroup;
  results: EventDTO[] = [];
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.searchForm = this.fb.group({
      city: ['', [Validators.required]],
      maxPrice: [
        null,
        [
          Validators.min(1)
        ]
      ],
      eventType: [null],
      dateFrom: ['', [Validators.required, this.futureDateValidator]],
      dateTo: ['', [Validators.required, this.futureDateValidator]]
    }, {
      validators: this.dateRangeValidator
    });
  }

  onSubmit(): void {
    this.error = '';

    if (this.searchForm.invalid) {
      this.error = 'Форма містить помилки. Виправте поля і спробуйте знову.';
      return;
    }

    const payload = this.searchForm.value;

    this.searchService.searchEvents(payload).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => {
        console.error(err);
        this.error = err.message;
      }
    });
  }

  futureDateValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;

    const selectedDate = new Date(value);
    const now = new Date();

    if (selectedDate < now) {
      return { dateInPast: true };
    }

    return null;
  }

  dateRangeValidator(group: AbstractControl): ValidationErrors | null {
    const dateFromControl = group.get('dateFrom');
    const dateToControl = group.get('dateTo');

    if (!dateFromControl || !dateToControl) return null;

    if (dateFromControl.invalid || dateToControl.invalid) {
      return null;
    }

    const fromVal = dateFromControl.value;
    const toVal = dateToControl.value;
    if (!fromVal || !toVal) return null; // required-логіка обробляється окремо

    const fromDate = new Date(fromVal);
    const toDate = new Date(toVal);

    if (fromDate > toDate) {
      return { invalidDateRange: true };
    }

    return null;
  }

  trackAndOpen(event: EventDTO, e: MouseEvent): void {
    e.preventDefault();

    this.searchService.trackEvent(event.id).subscribe({
      next: () => {
        if (event.url) {
          window.open(event.url, '_blank');
        }
      },
      error: (err) => {
        console.error(err);
        if (event.url) {
          window.open(event.url, '_blank');
        }
      }
    });
  }

  get dateFromCtrl() { return this.searchForm.get('dateFrom'); }
  get dateToCtrl() { return this.searchForm.get('dateTo'); }
  get cityCtrl() { return this.searchForm.get('city'); }
  get maxPriceCtrl() { return this.searchForm.get('maxPrice'); }
}
