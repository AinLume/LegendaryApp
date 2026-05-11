import {useCallback, useEffect, useState} from 'react';
import { tablesApi } from '../../../../api';
import type {Table} from '../../../../types';

export const useTables = () => {
  const [data, setData] = useState<Table[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchTables = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await tablesApi.getAll();
      setData(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Неизвестная ошибка');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTables();
  }, []);

  return { data, isLoading, error, refetch: fetchTables };
};
