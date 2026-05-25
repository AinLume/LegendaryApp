import type { FC } from 'react';
import type { HourlyDataPoint } from '../../../../types';

export interface IProps {
  data: HourlyDataPoint[];
}

const formatHour = (hour: number): string => {
  return `${hour}:00`;
};

export const HourlyChart: FC<IProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return null;
  }

  const maxOrders = Math.max(...data.map((d) => d.orderCount), 1);
  const maxRevenue = Math.max(...data.map((d) => d.totalRevenue), 1);

  const chartHeight = 200;
  const barWidth = 12;
  const gap = 4;

  return (
    <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-lg font-semibold text-gray-900">Почасовая загрузка</h3>
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-primary" />
            <span className="text-sm text-gray-600">Заказы</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-secondary" />
            <span className="text-sm text-gray-600">Выручка</span>
          </div>
        </div>
      </div>

      <div className="space-y-6">
        <div>
          <p className="text-xs text-gray-500 mb-3">Количество заказов</p>
          <div className="relative h-40">
            <div className="absolute inset-0 flex items-end justify-between gap-1">
              {data.map((item) => {
                const height = (item.orderCount / maxOrders) * chartHeight;
                return (
                  <div
                    key={item.hour}
                    className="flex-1 flex flex-col items-center group"
                  >
                    <div className="relative w-full h-40 flex items-end">
                      <div
                        className="w-full bg-primary rounded-t transition-all hover:opacity-80"
                        style={{ height: `${Math.max(height, 4)}px` }}
                      />
                      <div className="absolute -top-8 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-900 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
                        {item.orderCount}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <div className="flex justify-between mt-2">
            {data
              .filter((_, i) => i % 3 === 0)
              .map((item) => (
                <span key={item.hour} className="text-xs text-gray-400">
                  {formatHour(item.hour)}
                </span>
              ))}
          </div>
        </div>

        <div>
          <p className="text-xs text-gray-500 mb-3">Выручка (₽)</p>
          <div className="relative h-40">
            <div className="absolute inset-0 flex items-end justify-between gap-1">
              {data.map((item) => {
                const height = (item.totalRevenue / maxRevenue) * chartHeight;
                return (
                  <div
                    key={item.hour}
                    className="flex-1 flex flex-col items-center group"
                  >
                    <div className="relative w-full h-40 flex items-end">
                      <div
                        className="w-full bg-secondary rounded-t transition-all hover:opacity-80"
                        style={{ height: `${Math.max(height, 4)}px` }}
                      />
                      <div className="absolute -top-8 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-900 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
                        {new Intl.NumberFormat('ru-RU').format(item.totalRevenue)} ₽
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <div className="flex justify-between mt-2">
            {data
              .filter((_, i) => i % 3 === 0)
              .map((item) => (
                <span key={item.hour} className="text-xs text-gray-400">
                  {formatHour(item.hour)}
                </span>
              ))}
          </div>
        </div>
      </div>
    </div>
  );
};
