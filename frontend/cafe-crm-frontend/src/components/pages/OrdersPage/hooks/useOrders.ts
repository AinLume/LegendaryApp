import {useCallback, useEffect, useState} from 'react';
import { ordersApi } from '../../../../api';
import type {Order, OrdersQueryParams, PageResponse} from '../../../../types';

export const useOrders = () => {
  const [data, setData] = useState<PageResponse<Order> | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOrders = useCallback(async (params?: OrdersQueryParams) => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await ordersApi.getAll(params);
      setData(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Неизвестная ошибка');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
      fetchOrders()
  }, []);

  return { data, isLoading, error, fetchOrders };
};
