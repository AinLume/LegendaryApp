import type {FC} from 'react';
import { useState } from 'react';
import { Modal, Input, Button } from '../../../ui';

export interface IProps {
  isOpen: boolean;
  onClose: () => void;
  onCreate: (capacity: number) => void;
}

export const CreateTableModal: FC<IProps> = ({ isOpen, onClose, onCreate }) => {
  const [capacity, setCapacity] = useState('4');

  const handleSubmit = () => {
    const caps = parseInt(capacity, 10);
    if (caps > 0) {
      onCreate(caps);
      setCapacity('4');
      onClose();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Добавить стол"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={handleSubmit}>Создать</Button>
        </>
      }
    >
      <Input
        id="capacity"
        label="Количество персон"
        type="number"
        min="1"
        max="20"
        value={capacity}
        onChange={(e) => setCapacity(e.target.value)}
      />
    </Modal>
  );
};
