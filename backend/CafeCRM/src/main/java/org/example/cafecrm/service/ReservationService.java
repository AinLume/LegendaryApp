package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.reservation.CreateReservationRequest;
import org.example.cafecrm.domain.dto.reservation.ReservationResponse;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.domain.entity.Reservation;
import org.example.cafecrm.domain.entity.Staff;
import org.example.cafecrm.domain.entity.Tables;
import org.example.cafecrm.domain.values.ReservationStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.ReservationMapper;
import org.example.cafecrm.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис управления бронированиями столиков.
 * <p>
 * Обеспечивает создание, отмену и просмотр бронирований ({@link Reservation}),
 * а также поиск доступных столиков на заданный временной интервал.
 * <p>
 * При создании бронирования выполняется валидация вместимости столика
 * и проверка отсутствия конфликтующих бронирований на выбранное время.
 * <p>
 * Отмена возможна только для активных бронирований ({@link ReservationStatus#ACTIVE})
 * и только до момента окончания бронирования.
 *
 * @author AinLume
 * @see Reservation
 * @see Tables
 * @see TableService
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final TableService tableService;
    private final StaffService staffService;

    /**
     * Возвращает сущность бронирования по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса.
     *
     * @param id идентификатор бронирования
     * @return найденная сущность {@link Reservation}
     * @throws NotFoundException если бронирование с указанным id не существует
     */
    @Transactional(readOnly = true)
    public Reservation getEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reservation with id %d does not exist", id)));
    }

    /**
     * Возвращает DTO бронирования по идентификатору.
     * <p>
     * Переиспользует {@link #getEntityById(Long)} для получения сущности.
     *
     * @param id идентификатор бронирования
     * @return DTO с данными бронирования
     * @throws NotFoundException если бронирование с указанным id не существует
     */
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return reservationMapper.toResponse(getEntityById(id));
    }

    /**
     * Возвращает все бронирования.
     *
     * @return список DTO всех бронирований;
     *         пустой список, если бронирований нет
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает все бронирования для указанного столика.
     *
     * @param tableId идентификатор столика
     * @return список DTO бронирований для столика;
     *         пустой список, если бронирований нет
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getByTableId(Long tableId) {
        return reservationRepository.findByTableIdOrderByStartTimeAsc(tableId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает доступные столики на указанный временной интервал.
     * <p>
     * Фильтрует столики по вместимости (должна быть не меньше запрошенного
     * количества персон) и проверяет отсутствие конфликтующих бронирований.
     * <p>
     * Временной интервал должен быть корректным: {@code startTime} строго
     * раньше {@code endTime}.
     *
     * @param startTime начало временного интервала
     * @param endTime   конец временного интервала
     * @param persons   требуемое количество мест
     * @return список кратких DTO доступных столиков;
     *         пустой список, если подходящих столиков нет
     * @throws ConflictException если {@code startTime} не раньше {@code endTime}
     */
    @Transactional(readOnly = true)
    public List<TableShortResponse> getAvailableTables(LocalDateTime startTime, LocalDateTime endTime, Integer persons) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new ConflictException("Start time must be before end time");
        }

        List<TableResponse> allTables = tableService.findAll();

        return allTables.stream()
                .filter(table -> table.capacity() >= persons)
                .filter(table -> !reservationRepository.existsConflictingReservation(table.tableId(), startTime, endTime))
                .map(table -> new TableShortResponse(table.tableId(), table.number(), table.capacity()))
                .toList();
    }

    /**
     * Создаёт новое бронирование.
     * <p>
     * Выполняет валидации:
     * <ul>
     *   <li>Временной интервал корректен ({@code startTime} &lt; {@code endTime})</li>
     *   <li>Вместимость столика не меньше количества персон</li>
     *   <li>Столик свободен на запрошенное время (нет конфликтующих бронирований)</li>
     * </ul>
     * <p>
     * Назначает сотрудника, оформившего бронирование, и устанавливает
     * начальный статус {@link ReservationStatus#ACTIVE}.
     *
     * @param request данные для создания бронирования
     * @param staffId идентификатор сотрудника, оформляющего бронирование
     * @return DTO созданного бронирования
     * @throws NotFoundException если столик или сотрудник не найдены
     * @throws ConflictException если временной интервал некорректен,
     *                           вместимость недостаточна или столик занят
     */
    @Transactional
    public ReservationResponse create(CreateReservationRequest request, Long staffId) {
        if (request.startTime().isAfter(request.endTime()) || request.startTime().isEqual(request.endTime())) {
            throw new ConflictException("Start time must be before end time");
        }

        Tables table = tableService.getTableById(request.tableId());

        if (table.getCapacity() < request.persons()) {
            throw new ConflictException(
                    String.format("Table capacity (%d) is less than requested persons (%d)",
                            table.getCapacity(), request.persons())
            );
        }

        if (reservationRepository.existsConflictingReservation(table.getId(), request.startTime(), request.endTime())) {
            throw new ConflictException(
                    String.format("Table %s is already reserved for the requested time", table.getNumber())
            );
        }

        Staff staff = staffService.getEntityById(staffId);

        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setTable(table);
        reservation.setStaff(staff);

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponse(saved);
    }

    /**
     * Отменяет бронирование.
     * <p>
     * Переводит статус бронирования в {@link ReservationStatus#CANCELLED}.
     * <p>
     * Отмена невозможна, если:
     * <ul>
     *   <li>Бронирование уже отменено</li>
     *   <li>Время окончания бронирования уже прошло</li>
     * </ul>
     *
     * @param id идентификатор бронирования
     * @return DTO отменённого бронирования
     * @throws NotFoundException если бронирование не найдено
     * @throws ConflictException если бронирование уже отменено или является прошедшим
     */
    @Transactional
    public ReservationResponse cancel(Long id) {
        Reservation reservation = getEntityById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException("Reservation is already cancelled");
        }

        LocalDateTime now = LocalDateTime.now();
        if (reservation.getEndTime().isBefore(now)) {
            throw new ConflictException("Cannot cancel past reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);

        return reservationMapper.toResponse(updated);
    }

    /**
     * Проверяет, есть ли активное бронирование для столика прямо сейчас.
     *
     * @param tableId идентификатор столика
     * @return true если столик занят активным бронированием, false если свободен
     */
    @Transactional(readOnly = true)
    public boolean isTableOccupiedNow(Long tableId) {
        return reservationRepository.existsActiveReservationForTableNow(tableId, LocalDateTime.now());
    }
}