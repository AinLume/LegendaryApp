import type { FC, ReactNode } from 'react';

export interface Column<T> {
  key: string;
  title: string;
  render?: (value: unknown, row: T) => ReactNode;
  width?: string;
}

export interface Action<T> {
  label: string;
  icon?: ReactNode;
  onClick: (row: T) => void;
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
}

export interface IProps<T> {
  columns: Array<Column<T>>;
  data: Array<T>;
  actions?: Array<Action<T>>;
  keyExtractor: (row: T) => string;
  emptyMessage?: string;
  isLoading?: boolean;
}

export function Table<T>(props: IProps<T>) {
  const {
    columns,
    data,
    actions,
    keyExtractor,
    emptyMessage = 'Нет данных',
    isLoading = false,
  } = props;

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="text-center py-12 text-gray-500">
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {columns.map((column) => (
              <th
                key={column.key}
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                style={{ width: column.width }}
              >
                {column.title}
              </th>
            ))}
            {actions && actions.length > 0 && (
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Действия
              </th>
            )}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {data.map((row) => (
            <tr key={keyExtractor(row as T)} className="hover:bg-gray-50">
              {columns.map((column) => (
                <td
                  key={column.key}
                  className="px-6 py-4 whitespace-nowrap text-sm text-gray-900"
                >
                  {column.render
                    ? column.render((row as Record<string, unknown>)[column.key], row as T)
                    : String((row as Record<string, unknown>)[column.key] ?? '')}
                </td>
              ))}
              {actions && actions.length > 0 && (
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right">
                  <div className="flex justify-end gap-2">
                    {actions.map((action, idx) => (
                      <button
                        key={`${action.label}-${idx}`}
                        onClick={() => action.onClick(row as T)}
                        className={`text-xs px-2 py-1 rounded ${
                          action.variant === 'danger'
                            ? 'text-red-600 hover:bg-red-50'
                            : 'text-primary hover:bg-gray-100'
                        }`}
                      >
                        {action.icon && (
                          <span className="inline-flex items-center gap-1">
                            {action.icon}
                            {action.label}
                          </span>
                        )}
                        {!action.icon && action.label}
                      </button>
                    ))}
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
