import api from './baseApi';
import type {
  StaffLoginRequest,
  StaffLoginResponse,
  StaffRegisterRequest,
} from '../types/auth';

export const authApi = {
  loginStaff: async (dto: StaffLoginRequest): Promise<StaffLoginResponse> => {
    const response = await api.post<StaffLoginResponse>('/api/auth/login/staff', dto);
    return response.data;
  },

  registerStaff: async (dto: StaffRegisterRequest): Promise<StaffLoginResponse> => {
    const response = await api.post<StaffLoginResponse>('/api/auth/register/staff', dto);
    return response.data;
  },

  getMe: async (): Promise<StaffLoginResponse> => {
    const response = await api.get<StaffLoginResponse>('/api/auth/me/staff');
    return response.data;
  },

  logout: async (): Promise<void> => {
    await api.post('/api/auth/logout');
  },
};
