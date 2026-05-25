export interface AverageCheckResponse {
  totalRevenue: number;
  totalOrders: number;
  averageCheck: number;
  periodStart: string;
  periodEnd: string;
}

export interface HourlyDataPoint {
  hour: number;
  orderCount: number;
  totalRevenue: number;
}

export interface HourlyLoadResponse {
  periodStart: string;
  periodEnd: string;
  hourlyData: HourlyDataPoint[];
}

export interface TopItem {
  menuItemId: number;
  itemName: string;
  categoryName: string;
  totalOrdered: number;
  totalQuantity: number;
  totalRevenue: number;
}

export interface PopularItemsResponse {
  periodStart: string;
  periodEnd: string;
  topItems: TopItem[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface AnalyticsParams {
  start: string;
  end: string;
  page?: number;
  size?: number;
}
