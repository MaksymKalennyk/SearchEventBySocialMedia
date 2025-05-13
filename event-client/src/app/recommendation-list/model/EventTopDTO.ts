import {TicketOptionDTO} from '../../search/model/TicketOptionDTO';

export interface EventTopDTO {
  id: number;
  rawText: string;
  eventType: string;
  eventDateTime: string;
  city: string;
  url: string;
  ticketOptions: TicketOptionDTO[];
}
