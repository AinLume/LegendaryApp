import type { FC } from 'react';
import { useState, useEffect } from 'react';
import { Modal, Input, Button } from '../../../ui';
import type { MenuCategory } from '../../../../types';

export interface IProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: { name: string }) => void;
  category?: MenuCategory | null;
}

export const CategoryModal: FC<IProps> = ({ isOpen, onClose, onSave, category }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    if (category) {
      setName(category.name);
    } else {
      setName('');
    }
  }, [category, isOpen]);

  const handleSubmit = () => {
    if (name.trim()) {
      onSave({ name: name.trim() });
      setName('');
      onClose();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={category ? 'Редактировать категорию' : 'Создать категорию'}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={handleSubmit} disabled={!name.trim()}>
            {category ? 'Сохранить' : 'Создать'}
          </Button>
        </>
      }
    >
      <Input
        id="category-name"
        label="Название"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Введите название категории"
      />
    </Modal>
  );
};
