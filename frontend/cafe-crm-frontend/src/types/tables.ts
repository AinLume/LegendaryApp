export const TableStatus = {
  FREE: 'FREE',
  OCCUPIED: 'OCCUPIED',
  RESERVED: 'RESERVED',
} as const;

export type TableStatus = (typeof TableStatus)[keyof typeof TableStatus];

export interface Table {
  tableId: number;
  number: number;
  capacity: number;
  posX?: number;
  posY?: number;
  status: TableStatus;
}

export interface CreateTableDto {
  number: number;
  capacity: number;
  posX: number;
  posY: number;
}

export interface UpdateTablePositionDto {
  posX: number;
  posY: number;
}

export interface UpdateTableStatusDto {
  status: TableStatus;
}
