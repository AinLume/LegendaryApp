package org.example.cafecrm.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.dto.table.UpdateTablePositionRequest;
import org.example.cafecrm.domain.dto.table.UpdateTableStatusRequest;
import org.example.cafecrm.domain.entity.Tables;
import org.example.cafecrm.domain.values.TableStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.TableMapper;
import org.example.cafecrm.repository.TableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис управления столиками.
 * <p>
 * Обеспечивает CRUD-операции для сущности {@link Tables}:
 * создание, получение, обновление статуса и позиции, удаление.
 * <p>
 * При создании столика проверяет уникальность номера и устанавливает
 * начальный статус {@link TableStatus#FREE}.
 * <p>
 * Обновление статуса и позиции являются идемпотентными операциями:
 * если новое значение совпадает с текущим, сохранение не выполняется.
 * <p>
 * Используется {@link ReservationService} для поиска доступных столиков
 * и {@link OrderService} для привязки заказов к столикам.
 *
 * @author AinLume
 * @see Tables
 * @see TableMapper
 * @see ReservationService
 * @see OrderService
 */
@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final TableMapper mapper;

    /**
     * Возвращает сущность столика по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса
     * и внешними сервисами (например, {@link OrderService}, {@link ReservationService}).
     *
     * @param id идентификатор столика
     * @return найденная сущность {@link Tables}
     * @throws NotFoundException если столик с указанным id не существует
     */
    @Transactional(readOnly = true)
    public Tables getTableById(Integer id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Table with id %d does not exist", id)));
    }

    /**
     * Создаёт новый столик.
     * <p>
     * Проверяет уникальность номера столика. При дублировании выбрасывает
     * исключение. Устанавливает начальный статус {@link TableStatus#FREE}.
     *
     * @param dto данные для создания столика
     * @return DTO созданного столика
     * @throws ConflictException если столик с таким номером уже существует
     */
    @Transactional
    public TableResponse create(CreateTableRequest dto) {

        if (tableRepository.existsByNumber(dto.number())) {
            throw new ConflictException(
                    String.format("Table with number %s already exists", dto.number())
            );
        }

        Tables table = mapper.toEntity(dto);
        table.setStatus(TableStatus.FREE);

        return mapper.toResponse(tableRepository.save(table));
    }

    /**
     * Возвращает все столики.
     * <p>
     * Статус столика вычисляется динамически: если есть активное бронирование
     * на текущий момент, столик считается занятым (OCCUPIED), иначе свободным (FREE).
     *
     * @return список DTO всех столиков;
     *         пустой список, если столиков нет
     */
    @Transactional(readOnly = true)
    public List<TableResponse> findAll() {
        return tableRepository.findAllWithCurrentStatus(LocalDateTime.now());
    }

    /**
     * Удаляет столик по идентификатору.
     * <p>
     * Выполняет физическое удаление записи из базы данных.
     * Проверяет существование столика перед удалением.
     *
     * @param id идентификатор удаляемого столика
     * @throws NotFoundException если столик с указанным id не существует
     */
    @Transactional
    public void deleteTableById(Integer id) {

        if (!tableRepository.existsById(id)) {
            throw new NotFoundException(String.format("Table with id %d does not exist", id));
        }

        tableRepository.deleteById(id);
    }

    /**
     * Обновляет статус столика.
     * <p>
     * Идемпотентная операция: если запрошенный статус совпадает
     * с текущим, возвращает текущий DTO без сохранения.
     *
     * @param id  идентификатор столика
     * @param dto запрос с новым статусом
     * @return DTO столика с обновлённым статусом
     * @throws NotFoundException если столик с указанным id не существует
     */
    @Transactional
    public TableResponse updateTableStatusById(Integer id, UpdateTableStatusRequest dto) {
        Tables table = getTableById(id);

        if (table.getStatus().equals(dto.status()))
            return mapper.toResponse(table);

        table.setStatus(dto.status());
        return mapper.toResponse(tableRepository.save(table));
    }

    /**
     * Обновляет позицию столика на плане зала.
     * <p>
     * Идемпотентная операция: если запрошенные координаты совпадают
     * с текущими, возвращает текущий DTO без сохранения.
     *
     * @param id  идентификатор столика
     * @param dto запрос с новыми координатами
     * @return DTO столика с обновлённой позицией
     * @throws NotFoundException если столик с указанным id не существует
     */
    @Transactional
    public TableResponse updateTablePosition(Integer id, UpdateTablePositionRequest dto) {
        Tables table = getTableById(id);

        if (table.getPosX().equals(dto.posX()) && table.getPosY().equals(dto.posY()))
            return mapper.toResponse(table);

        table.setPosX(dto.posX());
        table.setPosY(dto.posY());

        return mapper.toResponse(tableRepository.save(table));
    }
}