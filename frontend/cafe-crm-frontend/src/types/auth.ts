export const StaffRole = {
  ADMIN: 'ADMIN',
  WAITER: 'WAITER',
  COOK: 'COOK',
  BARTENDER: 'BARTENDER',
} as const;

export type StaffRole = (typeof StaffRole)[keyof typeof StaffRole];

export const StaffRoleLabels: Record<StaffRole, string> = {
  ADMIN: 'Администратор',
  WAITER: 'Официант',
  COOK: 'Повар',
  BARTENDER: 'Бармен',
};

export interface StaffLoginRequest {
  email: string;
  password: string;
}

export interface StaffRegisterRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  role: StaffRole;
}

export interface StaffLoginResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: StaffRole;
}
