import type {FC} from 'react';
import { TableStatus } from '../../../../types';
import { cn } from '../../../../utils';

export interface IProps {
  table: {
    tableId: number;
    number: number;
    capacity: number;
    posX: number;
    posY: number;
    status: TableStatus;
  };
  isEditing: boolean;
  isDragging?: boolean;
  onDragStart?: (e: React.DragEvent, tableId: number) => void;
  onDrag?: (e: React.DragEvent) => void;
  onDragEnd?: (e: React.DragEvent) => void;
  onDelete?: (tableId: number) => void;
}

const statusColors: Record<TableStatus, string> = {
  [TableStatus.FREE]: 'bg-green-100 border-green-300 text-green-800',
  [TableStatus.OCCUPIED]: 'bg-red-100 border-red-300 text-red-800',
  [TableStatus.RESERVED]: 'bg-yellow-100 border-yellow-300 text-yellow-800',
};

export const TableItem: FC<IProps> = ({
  table,
  isEditing,
  isDragging = false,
  onDragStart,
  onDrag,
  onDragEnd,
  onDelete,
}) => {
  return (
    <div
      draggable={isEditing}
      onDragStart={(e) => onDragStart?.(e, table.tableId)}
      onDrag={onDrag}
      onDragEnd={onDragEnd}
      className={cn(
        'absolute flex flex-col items-center justify-center',
        'w-16 h-16 rounded-lg border-2 shadow-sm cursor-pointer transition-all',
        statusColors[table.status],
        isEditing && 'cursor-move hover:scale-110',
        isDragging && 'opacity-50'
      )}
      style={{
        left: table.posX,
        top: table.posY,
      }}
    >
      <span className="text-lg font-bold">{table.number}</span>
      <span className="text-xs">{table.capacity} чел.</span>
      {isEditing && onDelete && (
        <button
          onClick={(e) => {
            e.stopPropagation();
            onDelete(table.tableId);
          }}
          className="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600"
        >
          ×
        </button>
      )}
    </div>
  );
};
