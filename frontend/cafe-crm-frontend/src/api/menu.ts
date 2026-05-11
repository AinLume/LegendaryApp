import { api } from './baseApi';
import type {
  MenuCategoryWithItems,
  CreateCategoryDto,
  CreateMenuItemDto,
  UpdateMenuItemDto,
} from '../types';

export const menuApi = {
  // Get all categories with items
  getMenu: () =>
    api.get<MenuCategoryWithItems[]>('/api/menu'),

  // Create category
  createCategory: (data: CreateCategoryDto) =>
    api.post<{ menuCategoryId: number; name: string }>('/api/menu/categories', data),

  // Delete category
  deleteCategory: (id: number) =>
    api.delete(`/api/menu/categories/${id}`),

  // Create menu item
  createMenuItem: (data: CreateMenuItemDto) =>
    api.post<{
      menuItemId: number;
      categoryId: number;
      name: string;
      description: string;
      price: number;
      photoUrl?: string;
      type: string;
      isAvailable: boolean;
    }>('/api/menu/items', data),

  // Update menu item
  updateMenuItem: (id: number, data: UpdateMenuItemDto) =>
    api.put<{
      menuItemId: number;
      categoryId: number;
      name: string;
      description: string;
      price: number;
      photoUrl?: string;
      type: string;
      isAvailable: boolean;
    }>(`/api/menu/items/${id}`, data),

  // Delete menu item
  deleteMenuItem: (id: number) =>
    api.delete(`/api/menu/items/${id}`),
};
