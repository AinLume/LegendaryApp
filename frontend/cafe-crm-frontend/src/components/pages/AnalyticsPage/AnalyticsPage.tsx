import { useState, useMemo } from 'react';
import type { FC } from 'react';
import { useAnalytics } from './hooks/useAnalytics';
import { MetricCard, HourlyChart, TopItemsTable, DateRangePicker } from './components';

export const AnalyticsPage: FC = () => {
  const today = new Date().toISOString().split('T')[0];
  const [startDate, setStartDate] = useState(today);
  const [endDate, setEndDate] = useState(today);

  const { averageCheck, hourlyLoad, popularItems, loading, error } = useAnalytics(startDate, endDate);

  const formatCurrency = (value: number): string => {
    if (value >= 1000000) {
      return (value / 1000000).toFixed(1) + 'M ₽';
    }
    if (value >= 1000) {
      return new Intl.NumberFormat('ru-RU').format(value) + ' ₽';
    }
    return value + ' ₽';
  };

  const metrics = useMemo(() => {
    if (!averageCheck) return null;
    return [
      {
        title: 'Выручка',
        value: formatCurrency(averageCheck.totalRevenue),
        icon: '💰',
        color: 'primary' as const,
      },
      {
        title: 'Заказы',
        value: averageCheck.totalOrders,
        icon: '📋',
        color: 'secondary' as const,
      },
      {
        title: 'Средний чек',
        value: formatCurrency(averageCheck.averageCheck),
        icon: '📊',
        color: 'green' as const,
      },
    ];
  }, [averageCheck]);

  if (error) {
    return (
      <div className="max-w-7xl mx-auto p-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">Аналитика</h1>
        <div className="bg-red-50 border border-red-200 rounded-xl p-4 text-red-700">
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Аналитика</h1>
        <DateRangePicker
          startDate={startDate}
          endDate={endDate}
          onStartDateChange={setStartDate}
          onEndDateChange={setEndDate}
        />
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
        </div>
      ) : (
        <div className="space-y-6">
          {metrics && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {metrics.map((metric) => (
                <MetricCard
                  key={metric.title}
                  title={metric.title}
                  value={metric.value}
                  icon={metric.icon}
                  color={metric.color}
                />
              ))}
            </div>
          )}

          {hourlyLoad && hourlyLoad.hourlyData.length > 0 && (
            <HourlyChart data={hourlyLoad.hourlyData} />
          )}

          {popularItems && popularItems.topItems.length > 0 && (
            <TopItemsTable items={popularItems.topItems} />
          )}

          {!averageCheck && !hourlyLoad && !popularItems && (
            <div className="bg-gray-50 rounded-xl p-12 text-center">
              <p className="text-gray-500">Нет данных за выбранный период</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
