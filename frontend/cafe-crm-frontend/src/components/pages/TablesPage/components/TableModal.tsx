import { useState, useEffect, type FC } from 'react';
import { ordersApi, reservationsApi } from '../../../../api';
import { TableStatus, OrderStatus, OrderItemStatus, ReservationType } from '../../../../types';
import type { Table, Order, Reservation } from '../../../../types';
import {Badge, Button, Input, Modal} from "../../../ui";

export interface IProps {
  isOpen: boolean;
  table: Table | null;
  onClose: () => void;
  onUpdate: () => void;
}

type Tab = 'info' | 'reservation' | 'orders';

export const TableModal: FC<IProps> = ({ isOpen, table, onClose, onUpdate }) => {
  const [activeTab, setActiveTab] = useState<Tab>('info');
  const [orders, setOrders] = useState<Order[]>([]);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(false);

  const [reservationForm, setReservationForm] = useState({
    guestName: '',
    guestPhone: '',
    persons: 2,
    date: new Date().toISOString().split('T')[0],
    time: '12:00',
    duration: 2,
    note: '',
  });
  const [reservationErrors, setReservationErrors] = useState<Record<string, string>>({});
  const [creatingReservation, setCreatingReservation] = useState(false);

  useEffect(() => {
    if (isOpen && table) {
      setActiveTab('info');
      fetchTableData();
    }
  }, [isOpen, table]);

  const fetchTableData = async () => {
    if (!table) return;
    setLoading(true);
    try {
      const [ordersData, reservationsData] = await Promise.all([
        ordersApi.getByTableId(table.tableId),
        reservationsApi.getByTableId(table.tableId).catch(() => []),
      ]);
      setOrders(ordersData);
      setReservations(reservationsData);
    } catch (error) {
      console.error('Ошибка загрузки данных:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateReservation = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!table) return;

    const errors: Record<string, string> = {};
    if (!reservationForm.guestName.trim()) errors.guestName = 'Имя обязательно';
    if (!reservationForm.guestPhone.trim()) errors.guestPhone = 'Телефон обязязателен';

    if (Object.keys(errors).length > 0) {
      setReservationErrors(errors);
      return;
    }

    setCreatingReservation(true);
    try {
      const [hours, minutes] = reservationForm.time.split(':').map(Number);
      const startTime = new Date(reservationForm.date);
      startTime.setHours(hours, minutes, 0, 0);

      const endTime = new Date(startTime);
      endTime.setHours(endTime.getHours() + reservationForm.duration);

      await reservationsApi.create({
        tableId: table.tableId,
        guestName: reservationForm.guestName,
        guestPhone: reservationForm.guestPhone,
        persons: reservationForm.persons,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        type: ReservationType.TABLE,
        note: reservationForm.note || undefined,
      });

      setReservationForm({
        guestName: '',
        guestPhone: '',
        persons: 2,
        date: new Date().toISOString().split('T')[0],
        time: '12:00',
        duration: 2,
        note: '',
      });
      setReservationErrors({});
      fetchTableData();
      onUpdate();
      setActiveTab('info');
    } catch (error) {
      setReservationErrors({
        general: error instanceof Error ? error.message : 'Ошибка создания брони',
      });
    } finally {
      setCreatingReservation(false);
    }
  };

  const handleCancelReservation = async (reservationId: number) => {
    if (!confirm('Отменить бронь?')) return;
    try {
      await reservationsApi.cancel(reservationId);
      fetchTableData();
      onUpdate();
    } catch (error) {
      console.error('Ошибка отмены брони:', error);
    }
  };

  const statusLabels: Record<TableStatus, string> = {
    [TableStatus.FREE]: 'Свободен',
    [TableStatus.OCCUPIED]: 'Занят',
    [TableStatus.RESERVED]: 'Забронирован',
  };

  const orderStatusLabels: Record<OrderStatus, string> = {
    [OrderStatus.NEW]: 'Новый',
    [OrderStatus.IN_PROGRESS]: 'В работе',
    [OrderStatus.READY]: 'Готов',
    [OrderStatus.PAID]: 'Оплачен',
    [OrderStatus.CANCELLED]: 'Отменён',
  };

  const activeOrders = orders.filter(
    (o) => o.status === OrderStatus.NEW || o.status === OrderStatus.IN_PROGRESS || o.status === OrderStatus.READY
  );
  const activeReservations = reservations.filter((r) => r.status === 'ACTIVE');

  if (!table) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Стол №${table.number}`} size="lg">
        <div className="flex border-b border-gray-200 mb-4">
          <button
            onClick={() => setActiveTab('info')}
            className={`px-4 py-2 font-medium text-sm border-b-2 transition-colors ${
              activeTab === 'info'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Информация
          </button>
          <button
            onClick={() => setActiveTab('reservation')}
            className={`px-4 py-2 font-medium text-sm border-b-2 transition-colors ${
              activeTab === 'reservation'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Бронирование
          </button>
          <button
            onClick={() => setActiveTab('orders')}
            className={`px-4 py-2 font-medium text-sm border-b-2 transition-colors relative ${
              activeTab === 'orders'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Заказы
            {activeOrders.length > 0 && (
              <span className="ml-2 bg-primary text-white text-xs px-2 py-0.5 rounded-full">
                {activeOrders.length}
              </span>
            )}
          </button>
        </div>

        {activeTab === 'info' && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-sm text-gray-500 mb-1">Статус</p>
                <span className={`inline-flex px-3 py-1 rounded-full text-sm font-medium ${
                  table.status === TableStatus.FREE
                    ? 'bg-green-100 text-green-800'
                    : table.status === TableStatus.OCCUPIED
                    ? 'bg-red-100 text-red-800'
                    : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {statusLabels[table.status]}
                </span>
              </div>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-sm text-gray-500 mb-1">Вместимость</p>
                <p className="text-lg font-semibold text-gray-900">{table.capacity} человек</p>
              </div>
            </div>

            {activeReservations.length > 0 && (
              <div>
                <h3 className="font-semibold text-gray-900 mb-3">Активные брони</h3>
                <div className="space-y-2">
                  {activeReservations.map((reservation) => (
                    <div key={reservation.reservationId} className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-medium text-gray-900">{reservation.guestName}</p>
                          <p className="text-sm text-gray-600">{reservation.guestPhone}</p>
                          <p className="text-sm text-gray-600 mt-1">
                            {new Date(reservation.startTime).toLocaleString('ru-RU', {
                              day: 'numeric',
                              month: 'short',
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                            {' – '}
                            {new Date(reservation.endTime).toLocaleString('ru-RU', {
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                          </p>
                          <p className="text-sm text-gray-600">{reservation.persons} чел.</p>
                          {reservation.note && (
                            <p className="text-sm text-gray-500 mt-1 italic">{reservation.note}</p>
                          )}
                        </div>
                        <button
                          onClick={() => handleCancelReservation(reservation.reservationId)}
                          className="text-red-600 hover:text-red-800 text-sm"
                        >
                          Отменить
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {activeOrders.length > 0 && (
              <div>
                <h3 className="font-semibold text-gray-900 mb-3">Активные заказы</h3>
                <div className="space-y-2">
                  {activeOrders.map((order) => (
                    <div key={order.orderId} className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-medium text-gray-900">Заказ #{order.orderId}</p>
                          <p className="text-sm text-gray-600">
                            {order.items.length} позиций · {new Intl.NumberFormat('ru-RU').format(order.totalAmount)} ₽
                          </p>
                          <p className="text-xs text-gray-500 mt-1">
                            {new Date(order.createdAt).toLocaleString('ru-RU')}
                          </p>
                        </div>
                        <Badge>{orderStatusLabels[order.status]}</Badge>
                      </div>
                      <div className="mt-2 space-y-1">
                        {order.items.map((item) => (
                          <div key={item.orderItemId} className="text-sm flex justify-between text-gray-600">
                            <span>{item.quantity}x {item.menuItem.name}</span>
                            <span className="text-xs">
                              {item.status === OrderItemStatus.NEW && '🔴 Новый'}
                              {item.status === OrderItemStatus.IN_PROGRESS && '🟡 Готовится'}
                              {item.status === OrderItemStatus.READY && '🟢 Готов'}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {activeOrders.length === 0 && activeReservations.length === 0 && (
              <div className="text-center py-8 text-gray-500">
                Нет активных броней и заказов
              </div>
            )}
          </div>
        )}

        {activeTab === 'reservation' && (
          <form onSubmit={handleCreateReservation} className="space-y-4">
            {reservationErrors.general && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3 text-red-700 text-sm">
                {reservationErrors.general}
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Имя гостя"
                placeholder="Иван Иванов"
                value={reservationForm.guestName}
                onChange={(e) => setReservationForm({ ...reservationForm, guestName: e.target.value })}
                error={reservationErrors.guestName}
              />
              <Input
                label="Телефон"
                placeholder="+79001234567"
                value={reservationForm.guestPhone}
                onChange={(e) => setReservationForm({ ...reservationForm, guestPhone: e.target.value })}
                error={reservationErrors.guestPhone}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Дата"
                type="date"
                value={reservationForm.date}
                onChange={(e) => setReservationForm({ ...reservationForm, date: e.target.value })}
                min={new Date().toISOString().split('T')[0]}
              />
              <Input
                label="Время"
                type="time"
                value={reservationForm.time}
                onChange={(e) => setReservationForm({ ...reservationForm, time: e.target.value })}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Количество человек (макс: {table.capacity})
                </label>
                <input
                  type="number"
                  min="1"
                  max={table.capacity}
                  value={reservationForm.persons}
                  onChange={(e) => setReservationForm({ ...reservationForm, persons: parseInt(e.target.value) || 1 })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary outline-none"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Длительность (часы)
                </label>
                <select
                  value={reservationForm.duration}
                  onChange={(e) => setReservationForm({ ...reservationForm, duration: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary outline-none bg-white"
                >
                  <option value={1}>1 час</option>
                  <option value={2}>2 часа</option>
                  <option value={3}>3 часа</option>
                  <option value={4}>4 часа</option>
                </select>
              </div>
            </div>

            <Input
              label="Примечание"
              placeholder="День рождения, особые пожелания..."
              value={reservationForm.note}
              onChange={(e) => setReservationForm({ ...reservationForm, note: e.target.value })}
            />

            <Button
              type="submit"
              variant="primary"
              disabled={creatingReservation}
              className="w-full"
            >
              {creatingReservation ? 'Создание...' : 'Забронировать'}
            </Button>
          </form>
        )}

        {activeTab === 'orders' && (
          <div>
            {loading ? (
              <div className="flex justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
              </div>
            ) : orders.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                Нет заказов для этого стола
              </div>
            ) : (
              <div className="space-y-3">
                {orders.map((order) => (
                  <div
                    key={order.orderId}
                    className={`border rounded-lg p-4 ${
                      order.status === OrderStatus.NEW
                        ? 'bg-blue-50 border-blue-200'
                        : order.status === OrderStatus.IN_PROGRESS
                        ? 'bg-yellow-50 border-yellow-200'
                        : order.status === OrderStatus.READY
                        ? 'bg-green-50 border-green-200'
                        : 'bg-gray-50 border-gray-200'
                    }`}
                  >
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <p className="font-semibold text-gray-900">Заказ #{order.orderId}</p>
                        <p className="text-sm text-gray-600">
                          {new Date(order.createdAt).toLocaleString('ru-RU')}
                        </p>
                      </div>
                      <Badge>{orderStatusLabels[order.status]}</Badge>
                    </div>

                    <div className="space-y-2 mb-3">
                      {order.items.map((item) => (
                        <div key={item.orderItemId} className="flex justify-between items-center text-sm">
                          <div className="flex items-center gap-2">
                            <span className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-medium bg-white">
                              {item.quantity}
                            </span>
                            <span className="text-gray-700">{item.menuItem.name}</span>
                            {item.comment && (
                              <span className="text-gray-500 text-xs italic">({item.comment})</span>
                            )}
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-gray-500">
                              {item.status === OrderItemStatus.NEW && '🔴'}
                              {item.status === OrderItemStatus.IN_PROGRESS && '🟡'}
                              {item.status === OrderItemStatus.READY && '🟢'}
                            </span>
                            <span className="font-medium text-gray-700">
                              {new Intl.NumberFormat('ru-RU').format(item.menuItem.price * item.quantity)} ₽
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>

                    <div className="flex justify-between items-center pt-3 border-t border-gray-200">
                      <span className="text-sm text-gray-600">Итого:</span>
                      <span className="text-lg font-bold text-primary">
                        {new Intl.NumberFormat('ru-RU').format(order.totalAmount)} ₽
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        <div className="mt-6 pt-4 border-t border-gray-200 flex justify-end gap-2">
          <Button variant="secondary" onClick={onClose}>
            Закрыть
          </Button>
        </div>
    </Modal>
  );
};
