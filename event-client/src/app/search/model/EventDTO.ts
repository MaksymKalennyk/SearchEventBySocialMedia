import {TicketOptionDTO} from './TicketOptionDTO';

export interface EventDTO {
  id: number;
  chatId: number | null;
  messageId: number | null;
  rawText: string | null;

  eventType: string;
  eventDateTime: string;
  city: string;
  url: string;
  createdAt: string;

  ticketOptions: TicketOptionDTO[];
}
