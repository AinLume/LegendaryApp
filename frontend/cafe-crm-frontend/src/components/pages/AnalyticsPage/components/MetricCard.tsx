import type { FC } from 'react';

export interface IProps {
  title: string;
  value: string | number;
  icon: string;
  color: 'primary' | 'secondary' | 'green' | 'orange';
  trend?: { value: number; isPositive: boolean };
}

const colorClasses = {
  primary: 'bg-primary/10 text-primary',
  secondary: 'bg-secondary/10 text-secondary',
  green: 'bg-green-100 text-green-700',
  orange: 'bg-orange-100 text-orange-700',
};

export const MetricCard: FC<IProps> = ({ title, value, icon, color, trend }) => {
  return (
    <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-500 mb-1">{title}</p>
          <p className="text-3xl font-bold text-gray-900">{value}</p>
          {trend && (
            <p className={`text-sm mt-2 ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
            </p>
          )}
        </div>
        <div className={`w-14 h-14 rounded-full flex items-center justify-center text-2xl ${colorClasses[color]}`}>
          {icon}
        </div>
      </div>
    </div>
  );
};
