import api from './baseApi';
import type {
    CreateTableDto,
    Table,
    UpdateTablePositionDto,
    UpdateTableStatusDto,
} from '../types';

export const tablesApi = {
  getAll: async (): Promise<Table[]> => {
    const response = await api.get<Table[]>('/api/tables');
    return response.data;
  },

  create: async (dto: CreateTableDto): Promise<Table> => {
    const response = await api.post<Table>('/api/tables', dto);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/tables/${id}`);
  },

  updatePosition: async (id: number, dto: UpdateTablePositionDto): Promise<Table> => {
    const response = await api.patch<Table>(`/api/tables/${id}/position`, dto);
    return response.data;
  },

  updateStatus: async (id: number, dto: UpdateTableStatusDto): Promise<Table> => {
    const response = await api.patch<Table>(`/api/tables/${id}/status`, dto);
    return response.data;
  },
};
