import { useState, useEffect, useCallback } from 'react';
import { menuApi } from '../../../../api';
import type { MenuCategoryWithItems, MenuItem } from '../../../../types';

interface MenuCategory {
  menuCategoryId: number;
  name: string;
}

export const useMenu = () => {
  const [categories, setCategories] = useState<MenuCategory[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await menuApi.getMenu();
      const data: MenuCategoryWithItems[] = response.data;

      const cats: MenuCategory[] = data.map((cat) => ({
        menuCategoryId: cat.menuCategoryId,
        name: cat.name,
      }));

      const items: MenuItem[] = data.flatMap((cat) =>
        cat.items.map((item) => ({
          ...item,
          categoryName: cat.name,
        }))
      );

      setCategories(cats);
      setMenuItems(items);
    } catch (err) {
      console.error('Failed to fetch menu:', err);
      setError('Не удалось загрузить меню');
    }
    setIsLoading(false);
  }, []);

  const createCategory = useCallback(async (data: { name: string }) => {
    try {
      await menuApi.createCategory(data);
      await fetchData();
    } catch (err) {
      console.error('Failed to create category:', err);
      throw err;
    }
  }, [fetchData]);

  const deleteCategory = useCallback(async (id: number) => {
    try {
      await menuApi.deleteCategory(id);
      await fetchData();
    } catch (err) {
      console.error('Failed to delete category:', err);
      throw err;
    }
  }, [fetchData]);

  const createMenuItem = useCallback(async (data: {
    categoryId: number;
    name: string;
    description: string;
    price: number;
    photoUrl?: string;
    type: string;
  }) => {
    try {
      await menuApi.createMenuItem(data);
      await fetchData();
    } catch (err) {
      console.error('Failed to create menu item:', err);
      throw err;
    }
  }, [fetchData]);

  const updateMenuItem = useCallback(async (id: number, data: {
    categoryId: number;
    name: string;
    description: string;
    price: number;
    photoUrl?: string;
    isAvailable: boolean;
    type: string;
  }) => {
    try {
      await menuApi.updateMenuItem(id, data);
      await fetchData();
    } catch (err) {
      console.error('Failed to update menu item:', err);
      throw err;
    }
  }, [fetchData]);

  const deleteMenuItem = useCallback(async (id: number) => {
    try {
      await menuApi.deleteMenuItem(id);
      await fetchData();
    } catch (err) {
      console.error('Failed to delete menu item:', err);
      throw err;
    }
  }, [fetchData]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return {
    categories,
    menuItems,
    isLoading,
    error,
    createCategory,
    deleteCategory,
    createMenuItem,
    updateMenuItem,
    deleteMenuItem,
    refetch: fetchData,
  };
};
