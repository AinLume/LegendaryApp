import type {FC} from 'react';
import type {Order} from '../../../../types';
import { Badge } from '../../../ui';
import {formatDateTime, formatPrice, getOrderStatusVariant} from '../../../../utils';
import { OrderStatus } from '../../../../types';

export interface IProps {
  order: Order;
  onCancel?: (orderId: number) => void;
  onClose?: (orderId: number) => void;
}

export const OrderCard: FC<IProps> = ({ order, onCancel, onClose }) => {
  const canCancel = order.status !== OrderStatus.PAID && order.status !== OrderStatus.CANCELLED;
  const canClose = order.status === OrderStatus.READY;

  return (
    <div className="bg-white rounded-lg shadow-md p-4 border border-gray-200">
      <div className="flex justify-between items-start mb-3">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">
            Заказ #{order.orderId}
          </h3>
          <p className="text-sm text-gray-500">
            {formatDateTime(order.createdAt)}
          </p>
        </div>
        <Badge variant={getOrderStatusVariant(order.status)}>
          {getStatusText(order.status)}
        </Badge>
      </div>

      <div className="mb-3">
        <p className="text-sm text-gray-600">
          <span className="font-medium">Тип:</span> {order.type === 'DINE_IN' ? 'В зале' : 'Доставка'}
        </p>
        {order.table && (
          <p className="text-sm text-gray-600">
            <span className="font-medium">Стол:</span> #{order.table.number} ({order.table.capacity} чел.)
          </p>
        )}
      </div>

      <div className="border-t border-gray-200 pt-3 mb-3">
        <h4 className="text-sm font-medium text-gray-700 mb-2">Позиции:</h4>
        <ul className="space-y-1">
          {order.items.map((item) => (
            <li key={item.orderItemId} className="text-sm flex justify-between">
              <span>
                {item.menuItem.name} × {item.quantity}
              </span>
              <span className="text-gray-600">
                {formatPrice(item.menuItem.price * item.quantity)}
              </span>
            </li>
          ))}
        </ul>
      </div>

      <div className="flex justify-between items-center pt-3 border-t border-gray-200">
        <span className="text-lg font-semibold text-gray-900">
          Итого: {formatPrice(order.totalAmount)}
        </span>
        <div className="flex gap-2">
          {canCancel && onCancel && (
            <button
              onClick={() => onCancel(order.orderId)}
              className="px-3 py-1.5 text-sm text-red-600 hover:text-red-800 hover:bg-red-50 rounded transition-colors"
            >
              Отменить
            </button>
          )}
          {canClose && onClose && (
            <button
              onClick={() => onClose(order.orderId)}
              className="px-3 py-1.5 text-sm bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
            >
              Оплатить
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

const getStatusText = (status: OrderStatus): string => {
  switch (status) {
    case OrderStatus.NEW:
      return 'Новый';
    case OrderStatus.IN_PROGRESS:
      return 'В процессе';
    case OrderStatus.READY:
      return 'Готов';
    case OrderStatus.PAID:
      return 'Оплачен';
    case OrderStatus.CANCELLED:
      return 'Отменён';
    default:
      return status;
  }
};
