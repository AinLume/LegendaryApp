import type {FC} from 'react';
import { cn } from '../../utils';

export type BadgeVariant = 'success' | 'warning' | 'error' | 'info' | 'neutral';

export interface IProps {
  children: string;
  variant?: BadgeVariant;
}

const variantStyles: Record<BadgeVariant, string> = {
  success: 'bg-green-100 text-green-800 border-green-200',
  warning: 'bg-yellow-100 text-yellow-800 border-yellow-200',
  error: 'bg-red-100 text-red-800 border-red-200',
  info: 'bg-blue-100 text-blue-800 border-blue-200',
  neutral: 'bg-gray-100 text-gray-800 border-gray-200',
};

export const Badge: FC<IProps> = ({ children, variant = 'neutral' }) => {
  return (
    <span
      className={cn(
        'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border',
        variantStyles[variant]
      )}
    >
      {children}
    </span>
  );
};