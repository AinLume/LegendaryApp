import {OrderStatus} from "../types/orders.ts";
import type {BadgeVariant} from "../components/ui/Badge.tsx";

export const getOrderStatusVariant = (status: OrderStatus): BadgeVariant => {
    switch (status) {
        case OrderStatus.NEW:
            return 'info';
        case OrderStatus.IN_PROGRESS:
            return 'warning';
        case OrderStatus.READY:
            return 'success';
        case OrderStatus.PAID:
            return 'neutral';
        case OrderStatus.CANCELLED:
            return 'error';
        default:
            return 'neutral';
    }
};