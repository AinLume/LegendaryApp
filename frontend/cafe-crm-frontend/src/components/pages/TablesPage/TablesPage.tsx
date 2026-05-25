import { useState, type FC } from 'react';
import { Button } from '../../ui';
import { useTables } from './hooks/useTables';
import { TableItem } from './components/TableItem';
import { CreateTableModal } from './components/CreateTableModal';
import { TableModal } from './components/TableModal';
import { tablesApi } from '../../../api';
import type { Table } from '../../../types';

const SCHEME_SIZE = 600;

export const TablesPage: FC = () => {
  const { data: tables, isLoading, refetch } = useTables();
  const [isEditing, setIsEditing] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedTable, setSelectedTable] = useState<Table | null>(null);
  const [draggedTable, setDraggedTable] = useState<Table | null>(null);
  const [dragPosition, setDragPosition] = useState({ x: 0, y: 0 });
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

  const handleCreateTable = async (capacity: number) => {
    try {
      const existingNumbers = tables.map((t) => t.number);
      let nextNumber = 1;
      while (existingNumbers.includes(nextNumber)) {
        nextNumber++;
      }

      await tablesApi.create({
        number: nextNumber,
        capacity,
        posX: 50,
        posY: 50,
      });
      refetch();
    } catch (error) {
      console.error('Ошибка создания стола:', error);
    }
  };

  const handleDeleteTable = async (tableId: number) => {
    if (!confirm('Удалить стол?')) return;

    try {
      await tablesApi.delete(tableId);
      refetch();
    } catch (error) {
      console.error('Ошибка удаления стола:', error);
    }
  };

  const handleTableClick = (tableId: number) => {
    const table = tables.find((t) => t.tableId === tableId);
    if (table) {
      setSelectedTable(table);
    }
  };

  const handleDragStart = (e: React.DragEvent, tableId: number) => {
    const table = tables.find((t) => t.tableId === tableId);
    if (!table) return;

    setDraggedTable(table);

    const rect = (e.target as HTMLElement).getBoundingClientRect();
    setDragOffset({
      x: e.clientX - rect.left,
      y: e.clientY - rect.top,
    });
  };

  const handleDrag = (e: React.DragEvent) => {
    if (!draggedTable) return;

    const container = (e.target as HTMLElement).parentElement;
    if (!container) return;

    const containerRect = container.getBoundingClientRect();
    const x = e.clientX - containerRect.left - dragOffset.x;
    const y = e.clientY - containerRect.top - dragOffset.y;

    setDragPosition({ x, y });
  };

  const handleDragEnd = async (e: React.DragEvent) => {
    if (!draggedTable) return;

    const container = (e.target as HTMLElement).parentElement;
    if (!container) return;

    const containerRect = container.getBoundingClientRect();
    let newX = e.clientX - containerRect.left - dragOffset.x;
    let newY = e.clientY - containerRect.top - dragOffset.y;

    newX = Math.trunc(Math.max(0, Math.min(newX, SCHEME_SIZE - 64)));
    newY = Math.trunc(Math.max(0, Math.min(newY, SCHEME_SIZE - 64)));

    try {
      await tablesApi.updatePosition(draggedTable.tableId, { posX: newX, posY: newY });
      refetch();
    } catch (error) {
      console.error('Ошибка обновления позиции:', error);
    } finally {
      setDraggedTable(null);
    }
  };

  const handleModalClose = () => {
    setSelectedTable(null);
  };

  const handleUpdate = () => {
    refetch();
  };

  if (isLoading) {
    return (
      <div>
        <div className="min-h-screen flex items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Схема зала</h1>
              <p className="text-gray-600 mt-1">Нажмите на стол для деталей, брони и заказов</p>
            </div>
            <div className="flex gap-2">
              <Button
                variant={isEditing ? 'danger' : 'secondary'}
                onClick={() => setIsEditing(!isEditing)}
              >
                {isEditing ? 'Выйти из редактирования' : 'Редактировать'}
              </Button>
              {isEditing && (
                <Button onClick={() => setIsModalOpen(true)}>
                  + Добавить стол
                </Button>
              )}
            </div>
          </div>

          <div className="flex justify-center">
            <div
              id="tables-container"
              className="relative bg-white rounded-lg shadow-lg border-2 border-gray-200"
              style={{ width: SCHEME_SIZE, height: SCHEME_SIZE }}
            >
              {tables.map((table) => {
                const isDragging = draggedTable?.tableId === table.tableId;
                const displayX = isDragging ? dragPosition.x : (table.posX ?? 0);
                const displayY = isDragging ? dragPosition.y : (table.posY ?? 0);

                return (
                  <TableItem
                    key={table.tableId}
                    table={{
                      tableId: table.tableId,
                      number: table.number,
                      capacity: table.capacity,
                      posX: displayX,
                      posY: displayY,
                      status: table.status,
                    }}
                    isEditing={isEditing}
                    isDragging={isDragging}
                    onDragStart={isEditing ? handleDragStart : undefined}
                    onDrag={isEditing ? handleDrag : undefined}
                    onDragEnd={isEditing ? handleDragEnd : undefined}
                    onDelete={isEditing ? handleDeleteTable : undefined}
                    onClick={handleTableClick}
                  />
                );
              })}
            </div>
          </div>

          <div className="mt-6 flex justify-center gap-6 text-sm">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded bg-green-100 border border-green-300" />
              <span>Свободен</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded bg-red-100 border border-red-300" />
              <span>Занят</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded bg-yellow-100 border border-yellow-300" />
              <span>Забронирован</span>
            </div>
          </div>
        </div>
      </div>

      <CreateTableModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onCreate={handleCreateTable}
      />

      <TableModal
        isOpen={selectedTable !== null}
        table={selectedTable}
        onClose={handleModalClose}
        onUpdate={handleUpdate}
      />
    </div>
  );
};
