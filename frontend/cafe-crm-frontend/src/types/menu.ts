export const MenuItemType = {
  FOOD: 'FOOD',
  DRINK: 'DRINK',
} as const;

export type MenuItemType = (typeof MenuItemType)[keyof typeof MenuItemType];

export interface MenuCategory {
  menuCategoryId: number;
  name: string;
}

export interface MenuItem {
  menuItemId: number;
  categoryId: number;
  name: string;
  description: string;
  price: number;
  photoUrl?: string;
  type: MenuItemType;
  isAvailable: boolean;
}

export interface MenuCategoryWithItems {
  menuCategoryId: number;
  name: string;
  items: MenuItem[];
}

export interface CreateCategoryDto {
  name: string;
}

export interface CreateMenuItemDto {
  categoryId: number;
  name: string;
  description: string;
  price: number;
  photoUrl?: string;
  type: MenuItemType;
}

export interface UpdateMenuItemDto {
  categoryId: number;
  name: string;
  description: string;
  price: number;
  photoUrl?: string;
  isAvailable: boolean;
  type: MenuItemType;
}
