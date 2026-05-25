import { useCallback, useEffect, useState, useMemo } from 'react';
import { tablesApi, reservationsApi } from '../../../../api';
import { TableStatus } from '../../../../types';
import type { Table, Reservation } from '../../../../types';

export const useTables = () => {
  const [data, setData] = useState<Table[]>([]);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [tablesData, reservationsData] = await Promise.all([
        tablesApi.getAll(),
        reservationsApi.getAll().catch(() => []),
      ]);
      setData(tablesData);
      setReservations(reservationsData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Неизвестная ошибка');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const tablesWithStatus = useMemo(() => {
    const now = new Date();
    const nowMs = now.getTime();
    const twoHoursMs = 2 * 60 * 60 * 1000;

    return data.map((table) => {
      const tableReservations = reservations.filter(
        (r) => r.table.tableId === table.tableId && r.status === 'ACTIVE'
      );

      let status = TableStatus.FREE;

      for (const reservation of tableReservations) {
        const startTime = new Date(reservation.startTime).getTime();
        const endTime = new Date(reservation.endTime).getTime();

        if (nowMs >= startTime && nowMs <= endTime) {
          status = TableStatus.OCCUPIED;
          break;
        }

        if (startTime > nowMs && startTime - nowMs <= twoHoursMs) {
          status = TableStatus.RESERVED;
        }
      }

      return { ...table, status };
    });
  }, [data, reservations]);

  return { data: tablesWithStatus, isLoading, error, refetch: fetchData };
};
