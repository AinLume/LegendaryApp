import api from './baseApi';
import type {
  Reservation,
  CreateReservationDto,
} from '../types';

export const reservationsApi = {
  getAll: async (): Promise<Reservation[]> => {
    const response = await api.get<Reservation[]>('/api/reservations');
    return response.data;
  },

  create: async (dto: CreateReservationDto): Promise<Reservation> => {
    const response = await api.post<Reservation>('/api/reservations', dto);
    return response.data;
  },

  cancel: async (id: number): Promise<Reservation> => {
    const response = await api.put<Reservation>(`/api/reservations/${id}/cancel`);
    return response.data;
  },

  getByTableId: async (tableId: number): Promise<Reservation[]> => {
    const response = await api.get<Reservation[]>(`/api/reservations/table/${tableId}`);
    return response.data;
  },
};
