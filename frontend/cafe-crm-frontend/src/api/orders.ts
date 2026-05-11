import api from './baseApi';
import type {
    CloseOrderDto,
    CreateOrderDto,
    Order,
    OrdersQueryParams,
    PageResponse,
    UpdateOrderItemStatusDto,
} from '../types';

export const ordersApi = {
  getAll: async (params?: OrdersQueryParams): Promise<PageResponse<Order>> => {
    const response = await api.get<PageResponse<Order>>('/api/orders', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Order> => {
    const response = await api.get<Order>(`/api/orders/${id}`);
    return response.data;
  },

  getByTableId: async (tableId: number): Promise<Order[]> => {
    const response = await api.get<Order[]>(`/api/orders/table/${tableId}`);
    return response.data;
  },

  create: async (dto: CreateOrderDto): Promise<Order> => {
    const response = await api.post<Order>('/api/orders', dto);
    return response.data;
  },

  close: async (id: number, dto: CloseOrderDto): Promise<Order> => {
    const response = await api.put<Order>(`/api/orders/${id}/close`, dto);
    return response.data;
  },

  cancel: async (id: number): Promise<Order> => {
    const response = await api.put<Order>(`/api/orders/${id}/cancel`);
    return response.data;
  },

  updateItemStatus: async (itemId: number, dto: UpdateOrderItemStatusDto): Promise<Order> => {
    const response = await api.put<Order>(`/api/orders/items/${itemId}/status`, dto);
    return response.data;
  },

  getKitchenItems: async (status?: string): Promise<Order[]> => {
    const response = await api.get<Order[]>('/api/orders/items/kitchen', {
      params: status ? { status } : undefined,
    });
    return response.data;
  },

  getBarItems: async (status?: string): Promise<Order[]> => {
    const response = await api.get<Order[]>('/api/orders/items/bar', {
      params: status ? { status } : undefined,
    });
    return response.data;
  },
};
