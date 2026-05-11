import type { FC } from 'react';
import { useState } from 'react';
import { Button, Table } from '../../ui';
import type { Column } from '../../ui';
import { useMenu } from './hooks/useMenu';
import { CategoryModal } from './components/CategoryModal';
import { MenuItemModal } from './components/MenuItemModal';
import type { MenuItemType } from '../../../types';

interface MenuCategory {
  menuCategoryId: number;
  name: string;
}

interface MenuItem {
  menuItemId: number;
  categoryId: number;
  name: string;
  description: string;
  price: number;
  photoUrl?: string;
  type: MenuItemType;
  isAvailable: boolean;
  categoryName?: string;
}

export const MenuPage: FC = () => {
  const {
    categories,
    menuItems,
    isLoading,
    createCategory,
    deleteCategory,
    createMenuItem,
    updateMenuItem,
    deleteMenuItem,
  } = useMenu();

  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
  const [isItemModalOpen, setIsItemModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<MenuItem | null>(null);

  const handleCreateCategory = (data: { name: string }) => {
    createCategory(data);
  };

  const handleDeleteCategory = (category: MenuCategory) => {
    if (confirm(`Удалить категорию "${category.name}"?`)) {
      deleteCategory(category.menuCategoryId);
    }
  };

  const handleCreateItem = (data: {
    categoryId: number;
    name: string;
    description: string;
    price: number;
    photoUrl?: string;
    type: MenuItemType;
    isAvailable?: boolean;
  }) => {
    if (editingItem) {
      updateMenuItem(editingItem.menuItemId, {
        categoryId: data.categoryId,
        name: data.name,
        description: data.description,
        price: data.price,
        photoUrl: data.photoUrl,
        type: data.type,
        isAvailable: data.isAvailable ?? true,
      });
      setEditingItem(null);
    } else {
      createMenuItem(data);
    }
  };

  const handleEditItem = (item: MenuItem) => {
    setEditingItem(item);
    setIsItemModalOpen(true);
  };

  const handleDeleteItem = (item: MenuItem) => {
    if (confirm(`Удалить позицию "${item.name}"?`)) {
      deleteMenuItem(item.menuItemId);
    }
  };

  const closeItemModal = () => {
    setIsItemModalOpen(false);
    setEditingItem(null);
  };

  const categoryColumns: Array<Column<MenuCategory>> = [
    {
      key: 'name',
      title: 'Название',
    },
  ];

  const itemColumns: Array<Column<MenuItem>> = [
    {
      key: 'name',
      title: 'Название',
    },
    {
      key: 'description',
      title: 'Описание',
      render: (value) => (
        <span className="truncate max-w-xs block">{String(value)}</span>
      ),
    },
    {
      key: 'price',
      title: 'Цена',
      render: (value) => `${Number(value).toFixed(2)} ₽`,
    },
    {
      key: 'categoryName',
      title: 'Категория',
    },
    {
      key: 'type',
      title: 'Тип',
      render: (value) => (value === 'FOOD' ? 'Еда' : 'Напиток'),
    },
    {
      key: 'isAvailable',
      title: 'Статус',
      render: (value) => (
        <span
          className={`px-2 py-1 rounded text-xs ${
            value ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
          }`}
        >
          {value ? 'Доступно' : 'Недоступно'}
        </span>
      ),
    },
  ];

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">Управление меню</h1>
      </div>

      {/* Categories Section */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-800">Категории</h2>
          <Button onClick={() => setIsCategoryModalOpen(true)}>Создать категорию</Button>
        </div>
        <Table
          columns={categoryColumns}
          data={categories}
          actions={[
            {
              label: 'Удалить',
              onClick: handleDeleteCategory,
              variant: 'danger',
            },
          ]}
          keyExtractor={(category) => String(category.menuCategoryId)}
          emptyMessage="Нет категорий"
          isLoading={isLoading}
        />
      </div>

      {/* Menu Items Section */}
      <div>
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-800">Позиции меню</h2>
          <Button onClick={() => setIsItemModalOpen(true)}>Создать позицию</Button>
        </div>
        <Table
          columns={itemColumns}
          data={menuItems}
          actions={[
            {
              label: 'Редактировать',
              onClick: handleEditItem,
            },
            {
              label: 'Удалить',
              onClick: handleDeleteItem,
              variant: 'danger',
            },
          ]}
          keyExtractor={(item) => String(item.menuItemId)}
          emptyMessage="Нет позиций меню"
          isLoading={isLoading}
        />
      </div>

      {/* Modals */}
      <CategoryModal
        isOpen={isCategoryModalOpen}
        onClose={() => setIsCategoryModalOpen(false)}
        onSave={handleCreateCategory}
      />
      <MenuItemModal
        isOpen={isItemModalOpen}
        onClose={closeItemModal}
        onSave={handleCreateItem}
        item={editingItem}
        categories={categories}
      />
    </div>
  );
};
