export const ReservationStatus = {
  ACTIVE: 'ACTIVE',
  CANCELLED: 'CANCELLED',
} as const;

export type ReservationStatus = (typeof ReservationStatus)[keyof typeof ReservationStatus];

export const ReservationType = {
  TABLE: 'TABLE',
  EVENT: 'EVENT',
} as const;

export type ReservationType = (typeof ReservationType)[keyof typeof ReservationType];

export interface Reservation {
  reservationId: number;
  table: {
    tableId: number;
    number: number;
    capacity: number;
  };
  guestName: string;
  guestPhone: string;
  persons: number;
  startTime: string;
  endTime: string;
  type: ReservationType;
  status: ReservationStatus;
  note: string | null;
  createdAt: string;
}

export interface CreateReservationDto {
  tableId: number;
  guestName: string;
  guestPhone: string;
  persons: number;
  startTime: string;
  endTime: string;
  type: ReservationType;
  note?: string;
}
