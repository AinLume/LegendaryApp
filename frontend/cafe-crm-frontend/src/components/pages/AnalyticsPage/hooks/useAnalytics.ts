import { useState, useEffect } from 'react';
import { analyticsApi } from '../../../../api/analytics';
import type { AverageCheckResponse, HourlyLoadResponse, PopularItemsResponse } from '../../../../types';

interface UseAnalyticsResult {
  averageCheck: AverageCheckResponse | null;
  hourlyLoad: HourlyLoadResponse | null;
  popularItems: PopularItemsResponse | null;
  loading: boolean;
  error: string | null;
  refetch: (startDate: string, endDate: string) => void;
}

export const useAnalytics = (startDate: string, endDate: string): UseAnalyticsResult => {
  const [averageCheck, setAverageCheck] = useState<AverageCheckResponse | null>(null);
  const [hourlyLoad, setHourlyLoad] = useState<HourlyLoadResponse | null>(null);
  const [popularItems, setPopularItems] = useState<PopularItemsResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async (start: string, end: string) => {
    setLoading(true);
    setError(null);
    try {
      const [avgCheck, hourly, items] = await Promise.all([
        analyticsApi.getAverageCheck({ start, end }),
        analyticsApi.getHourlyLoad({ start, end }),
        analyticsApi.getPopularItems({ start, end, size: 10 }),
      ]);
      setAverageCheck(avgCheck);
      setHourlyLoad(hourly);
      setPopularItems(items);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка загрузки данных');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (startDate && endDate) {
      fetchData(startDate, endDate);
    }
  }, [startDate, endDate]);

  return {
    averageCheck,
    hourlyLoad,
    popularItems,
    loading,
    error,
    refetch: fetchData,
  };
};
