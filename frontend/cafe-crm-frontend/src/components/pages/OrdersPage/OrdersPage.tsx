import {type FC, useState } from 'react';
import { Button } from '../../ui';
import { useOrders } from './hooks/useOrders';
import { OrderCard } from './components/OrderCard';
import { OrderStatus } from '../../../types';

export const OrdersPage: FC = () => {
  const [selectedStatus, setSelectedStatus] = useState<OrderStatus | undefined>();
  const { data, isLoading, error, fetchOrders } = useOrders();

  const handleCancelOrder = async (orderId: number) => {
    try {
      // Здесь будет вызов API для отмены
      console.log('Отмена заказа:', orderId);
      await fetchOrders();
    } catch (err) {
      console.error('Ошибка при отмене заказа:', err);
    }
  };

  const handleCloseOrder = async (orderId: number) => {
    try {
      // Здесь будет вызов API для закрытия заказа с выбором способа оплаты
      console.log('Закрытие заказа:', orderId);
      fetchOrders({});
    } catch (err) {
      console.error('Ошибка при закрытии заказа:', err);
    }
  };

  const handleChangeStatus = (value: OrderStatus | undefined) => {
      if (value === selectedStatus) return;

      setSelectedStatus(value);
      fetchOrders({ status: value });
  }

  const statuses: { value: OrderStatus; label: string }[] = [
    { value: OrderStatus.NEW, label: 'Новые' },
    { value: OrderStatus.IN_PROGRESS, label: 'В процессе' },
    { value: OrderStatus.READY, label: 'Готовые' },
    { value: OrderStatus.PAID, label: 'Оплаченные' },
    { value: OrderStatus.CANCELLED, label: 'Отменённые' },
  ];

  return (
    <div>
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold text-gray-900">Заказы</h1>
            <Button variant='primary' onClick={() => fetchOrders()}>Обновить</Button>
          </div>

          <div className="mb-6 flex gap-2 flex-wrap">
            <button
              onClick={() => handleChangeStatus(undefined)}
              className={`px-4 py-2 rounded-lg transition-colors ${
                selectedStatus === undefined
                  ? 'bg-primary text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              Все
            </button>
            {statuses.map((status) => (
              <button
                key={status.value}
                onClick={() => handleChangeStatus(status.value)}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  selectedStatus === status.value
                    ? 'bg-primary text-white'
                    : 'bg-white text-gray-700 hover:bg-gray-100'
                }`}
              >
                {status.label}
              </button>
            ))}
          </div>

          {isLoading && (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
            </div>
          )}

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
              Ошибка загрузки заказов: {error}
            </div>
          )}

          {!isLoading && !error && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {data?.content.map((order) => (
                <OrderCard
                  key={order.orderId}
                  order={order}
                  onCancel={handleCancelOrder}
                  onClose={handleCloseOrder}
                />
              ))}
            </div>
          )}

          {!isLoading && !error && data?.content.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              Нет заказов для отображения
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
