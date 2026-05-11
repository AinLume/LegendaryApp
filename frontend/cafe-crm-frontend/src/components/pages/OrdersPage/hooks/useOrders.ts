import {useCallback, useEffect, useState} from 'react';
import { ordersApi } from '../../../../api';
import type {Order, OrdersQueryParams, PageResponse} from '../../../../types';

export const useOrders = (params?: OrdersQueryParams) => {
  const [data, setData] = useState<PageResponse<Order> | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOrders = useCallback(async () => {
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
  }, [params]);

  useEffect(() => {
      if (params !== null && params !== undefined) {
          fetchOrders()
      }
  }, [fetchOrders, params]);

  return { data, isLoading, error, refetch: fetchOrders };
};
