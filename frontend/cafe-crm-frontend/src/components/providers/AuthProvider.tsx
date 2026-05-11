import type {FC, ReactNode} from 'react';
import { useState, useEffect } from 'react';
import { authApi } from '../../api/auth';
import {AuthContext, type IAuthContext, type StaffUser} from '../../types';

export interface IProps {
  children: ReactNode;
}

export const AuthProvider: FC<IProps> = ({ children }) => {
  const [user, setUser] = useState<StaffUser | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const checkAuth = async () => {
    try {
      setIsLoading(true);
      const response = await authApi.getMe();
      setUser(response);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Ошибка проверки аутентификации:', error);
      setUser(null);
      setIsAuthenticated(false);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    checkAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      setIsLoading(true);
      const response = await authApi.loginStaff({ email, password });
      setUser(response);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Ошибка при входе:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
      setUser(null);
      setIsAuthenticated(false);
    } catch (error) {
      console.error('Ошибка при выходе:', error);
    }
  };

  const value: IAuthContext = {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
