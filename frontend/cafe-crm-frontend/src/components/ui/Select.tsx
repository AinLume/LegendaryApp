import type { FC, SelectHTMLAttributes } from 'react';
import { cn } from '../../utils';

export interface IProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
}

export const Select: FC<IProps> = ({ label, error, className, children, id, ...props }) => {
  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={id}
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          {label}
        </label>
      )}
      <select
        id={id}
        className={cn(
          'w-full px-3 py-2 border border-gray-300 rounded-lg',
          'focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent',
          'disabled:bg-gray-100 disabled:cursor-not-allowed',
          'bg-white',
          error && 'border-red-500 focus:ring-red-500',
          className
        )}
        {...props}
      >
        {children}
      </select>
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
    </div>
  );
};
