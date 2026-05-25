import api from './baseApi';
import type {
  AverageCheckResponse,
  HourlyLoadResponse,
  PopularItemsResponse,
  AnalyticsParams,
} from '../types';

export const analyticsApi = {
  getAverageCheck: async (params: AnalyticsParams): Promise<AverageCheckResponse> => {
    const response = await api.get<AverageCheckResponse>('/api/analytics/average-check', { params });
    return response.data;
  },

  getHourlyLoad: async (params: AnalyticsParams): Promise<HourlyLoadResponse> => {
    const response = await api.get<HourlyLoadResponse>('/api/analytics/hourly-load', { params });
    return response.data;
  },

  getPopularItems: async (params: AnalyticsParams): Promise<PopularItemsResponse> => {
    const response = await api.get<PopularItemsResponse>('/api/analytics/popular-items', { params });
    return response.data;
  },
};
