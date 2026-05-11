import type { StaffRole } from './auth';
import {createContext} from "react";

export interface StaffUser {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: StaffRole;
}

export interface IAuthContext {
  user: StaffUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<void>;
}

export const AuthContext = createContext<IAuthContext | null>(null);