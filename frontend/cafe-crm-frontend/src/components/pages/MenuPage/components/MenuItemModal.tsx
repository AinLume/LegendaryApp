import type { FC } from 'react';
import { useState, useEffect } from 'react';
import { Modal, Input, Button } from '../../../ui';
import type { MenuItem, MenuCategory, MenuItemType } from '../../../../types';

export interface IProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: {
    categoryId: number;
    name: string;
    description: string;
    price: number;
    photoUrl?: string;
    type: MenuItemType;
    isAvailable?: boolean;
  }) => void;
  item?: MenuItem | null;
  categories: MenuCategory[];
}

export const MenuItemModal: FC<IProps> = ({ isOpen, onClose, onSave, item, categories }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [categoryId, setCategoryId] = useState<number>(0);
  const [photoUrl, setPhotoUrl] = useState('');
  const [type, setType] = useState<MenuItemType>('FOOD');
  const [isAvailable, setIsAvailable] = useState(true);

  useEffect(() => {
    if (item) {
      setName(item.name);
      setDescription(item.description);
      setPrice(String(item.price));
      setCategoryId(item.categoryId);
      setPhotoUrl(item.photoUrl || '');
      setType(item.type);
      setIsAvailable(item.isAvailable);
    } else {
      setName('');
      setDescription('');
      setPrice('');
      setCategoryId(categories[0]?.menuCategoryId || 0);
      setPhotoUrl('');
      setType('FOOD');
      setIsAvailable(true);
    }
  }, [item, isOpen, categories]);

  const handleSubmit = () => {
    if (name.trim() && price && categoryId) {
      onSave({
        categoryId,
        name: name.trim(),
        description: description.trim(),
        price: parseFloat(price) || 0,
        photoUrl: photoUrl.trim() || undefined,
        type,
        isAvailable,
      });
      setName('');
      setDescription('');
      setPrice('');
      setPhotoUrl('');
      setType('FOOD');
      setIsAvailable(true);
      onClose();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={item ? 'Редактировать позицию' : 'Создать позицию'}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={handleSubmit} disabled={!name.trim() || !price || !categoryId}>
            {item ? 'Сохранить' : 'Создать'}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <Input
          id="item-name"
          label="Название"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Введите название позиции"
        />
        <div>
          <label htmlFor="item-description" className="block text-sm font-medium text-gray-700 mb-1">
            Описание
          </label>
          <textarea
            id="item-description"
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Описание позиции"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
          />
        </div>
        <Input
          id="item-price"
          label="Цена"
          type="number"
          min="0"
          step="0.01"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          placeholder="0.00"
        />
        <div>
          <label htmlFor="item-category" className="block text-sm font-medium text-gray-700 mb-1">
            Категория
          </label>
          <select
            id="item-category"
            value={categoryId}
            onChange={(e) => setCategoryId(Number(e.target.value))}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            {categories.map((category) => (
              <option key={category.menuCategoryId} value={category.menuCategoryId}>
                {category.name}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="item-type" className="block text-sm font-medium text-gray-700 mb-1">
            Тип
          </label>
          <select
            id="item-type"
            value={type}
            onChange={(e) => setType(e.target.value as MenuItemType)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            <option value="FOOD">Еда</option>
            <option value="DRINK">Напиток</option>
          </select>
        </div>
        <Input
          id="item-photo-url"
          label="URL изображения"
          value={photoUrl}
          onChange={(e) => setPhotoUrl(e.target.value)}
          placeholder="https://example.com/image.jpg"
        />
        <div className="flex items-center gap-2">
          <input
            id="item-available"
            type="checkbox"
            checked={isAvailable}
            onChange={(e) => setIsAvailable(e.target.checked)}
            className="w-4 h-4 text-primary border-gray-300 rounded focus:ring-primary"
          />
          <label htmlFor="item-available" className="text-sm font-medium text-gray-700">
            Доступно для заказа
          </label>
        </div>
      </div>
    </Modal>
  );
};
