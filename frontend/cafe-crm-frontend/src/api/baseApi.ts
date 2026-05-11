import axios, { AxiosError, type AxiosResponse } from 'axios';

export const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          // Можно добавить редирект на страницу логина
          console.error('Unauthorized - требуется аутентификация');
          break;
        case 403:
          console.error('Forbidden - доступ запрещён');
          break;
        case 404:
          console.error('Not Found - ресурс не найден');
          break;
        default:
          console.error(`API Error: ${data?.message || 'Неизвестная ошибка'}`);
      }
    } else if (error.request) {
      console.error('Network Error - нет ответа от сервера');
    } else {
      console.error('Error:', error.message);
    }

    return Promise.reject(error);
  }
);

export interface ApiError {
  message: string;
}

export default api;
