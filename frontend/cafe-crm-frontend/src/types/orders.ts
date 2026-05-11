export const OrderStatus = {
  NEW: 'NEW',
  IN_PROGRESS: 'IN_PROGRESS',
  READY: 'READY',
  PAID: 'PAID',
  CANCELLED: 'CANCELLED',
} as const;

export type OrderStatus = (typeof OrderStatus)[keyof typeof OrderStatus];

export const OrderType = {
  DINE_IN: 'DINE_IN',
  DELIVERY: 'DELIVERY',
} as const;

export type OrderType = (typeof OrderType)[keyof typeof OrderType];

export const PaymentMethod = {
  CASH: 'CASH',
  CARD: 'CARD',
  ONLINE: 'ONLINE',
} as const;

export type PaymentMethod = (typeof PaymentMethod)[keyof typeof PaymentMethod];

export const OrderItemStatus = {
  NEW: 'NEW',
  IN_PROGRESS: 'IN_PROGRESS',
  READY: 'READY',
} as const;

export type OrderItemStatus = (typeof OrderItemStatus)[keyof typeof OrderItemStatus];

export const Destination = {
  KITCHEN: 'KITCHEN',
  BAR: 'BAR',
} as const;

export type Destination = (typeof Destination)[keyof typeof Destination];

export const MenuItemType = {
  FOOD: 'FOOD',
  DRINK: 'DRINK',
} as const;

export type MenuItemType = (typeof MenuItemType)[keyof typeof MenuItemType];

export interface MenuItem {
  menuItemId: number;
  categoryId: number;
  name: string;
  description: string;
  price: number;
  photoUrl: string;
  type: MenuItemType;
  isAvailable: boolean;
}

export interface OrderItem {
  orderItemId: number;
  orderId: number;
  menuItem: MenuItem;
  quantity: number;
  comment: string | null;
  status: OrderItemStatus;
  destination: Destination;
}

import type { Table } from './tables';

export interface Order {
  orderId: number;
  type: OrderType;
  tableId: number | null;
  table: Table | null;
  clientId: number | null;
  status: OrderStatus;
  totalAmount: number;
  paymentMethod: PaymentMethod | null;
  items: OrderItem[];
  createdAt: string;
  closedAt: string | null;
}

export interface CreateOrderItemDto {
  menuItemId: number;
  quantity: number;
  comment: string | null;
}

export interface CreateOrderDto {
  type: OrderType;
  tableId: number | null;
  clientId: number | null;
  deliveryAddress: string | null;
  items: CreateOrderItemDto[];
}

export interface CloseOrderDto {
  paymentMethod: PaymentMethod;
}

export interface OrdersQueryParams {
  status?: OrderStatus;
  clientId?: number;
  tableId?: number;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalPages: number;
  totalElements: number;
}

export interface UpdateOrderItemStatusDto {
  status: OrderItemStatus;
}
