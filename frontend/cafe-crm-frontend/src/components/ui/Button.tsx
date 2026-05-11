import type {FC, ButtonHTMLAttributes} from 'react';
import { cn } from '../../utils';

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'ghost';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface IProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  isLoading?: boolean;
  fullWidth?: boolean;
}

const variantStyles: Record<ButtonVariant, string> = {
  primary: 'bg-primary text-white hover:bg-primary/90 active:bg-primary/80',
  secondary: 'bg-secondary text-white hover:bg-secondary/90 active:bg-secondary/80',
  danger: 'bg-red-500 text-white hover:bg-red-600 active:bg-red-700',
  ghost: 'bg-transparent text-gray-700 hover:bg-gray-100 active:bg-gray-200',
};

const sizeStyles: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-base',
  lg: 'px-6 py-3 text-lg',
};

export const Button: FC<IProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  fullWidth = false,
  disabled,
  className,
  ...props
}) => {
  return (
    <button
      className={cn(
        'rounded-lg font-medium transition-colors duration-200',
        'disabled:opacity-50 disabled:cursor-not-allowed',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2',
        variantStyles[variant],
        sizeStyles[size],
        fullWidth && 'w-full',
        className
      )}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="flex items-center justify-center gap-2">
          <svg
            className="animate-spin h-4 w-4"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          Загрузка...
        </span>
      ) : (
        children
      )}
    </button>
  );
};

/*
Примеры использования:

import { Button } from './components/ui/Button';

// Базовая кнопка
<Button>Нажать</Button>

// С разными вариантами
<Button variant="secondary">Вторичная</Button>
<Button variant="danger">Удалить</Button>
<Button variant="ghost">Отмена</Button>

// С разными размерами
<Button size="sm">Маленькая</Button>
<Button size="lg">Большая</Button>

// Состоянием загрузки
<Button isLoading>Сохранение...</Button>

// На всю ширину
<Button fullWidth>Во всю ширину</Button>

// С дополнительными классами
<Button className="mt-4 shadow-lg">С кастомными стилями</Button>

// Отключённая
<Button disabled>Неактивна</Button>
*/
