export interface EventSearchRequest {
  city: string | null;
  maxPrice: number | null;
  eventType: string | null;
  dateFrom: string;
  dateTo: string;
}
