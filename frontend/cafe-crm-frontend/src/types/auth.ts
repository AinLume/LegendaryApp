export const StaffRole = {
  ADMIN: 'ADMIN',
  WAITER: 'WAITER',
  COOK: 'COOK',
  BARTENDER: 'BARTENDER',
} as const;

export type StaffRole = (typeof StaffRole)[keyof typeof StaffRole];

export interface StaffLoginRequest {
  email: string;
  password: string;
}

export interface StaffLoginResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: StaffRole;
}
