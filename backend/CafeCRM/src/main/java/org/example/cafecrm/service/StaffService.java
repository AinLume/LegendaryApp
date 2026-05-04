package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.staff.StaffCreateRequest;
import org.example.cafecrm.domain.dto.staff.StaffDto;
import org.example.cafecrm.domain.dto.staff.StaffUpdateRequest;
import org.example.cafecrm.domain.entity.Staff;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.StaffMapper;
import org.example.cafecrm.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис управления сотрудниками.
 * <p>
 * Обеспечивает CRUD-операции для сущности {@link Staff}.
 * Предоставляет методы для создания, получения, обновления и удаления
 * записей сотрудников, а также для получения сущности по идентификатору
 * для использования в других сервисах.
 * <p>
 * Использует {@link StaffMapper} для преобразования между сущностью и DTO.
 *
 * @author AinLume
 * @see Staff
 * @see StaffMapper
 */
@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    /**
     * Возвращает сущность сотрудника по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса
     * и внешними сервисами (например, {@link OrderService}, {@link ReservationService}).
     *
     * @param id идентификатор сотрудника
     * @return найденная сущность {@link Staff}
     * @throws NotFoundException если сотрудник с указанным id не существует
     */
    @Transactional(readOnly = true)
    public Staff getEntityById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff not found with id: " + id));
    }

    /**
     * Возвращает сотрудника по адресу электронной почты.
     * <p>
     * Используется в процессе аутентификации ({@code Auth}) для поиска
     * существующего сотрудника по предоставленному email.
     *
     * @param email адрес электронной почты сотрудника
     * @return найденная сущность {@link Staff} или {@code null}, если сотрудник не найден
     * @see org.example.cafecrm.controller.AuthController
     */
    @Transactional(readOnly = true)
    public Staff getStaffByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    /**
     * Возвращает DTO сотрудника по идентификатору.
     * <p>
     * Переиспользует {@link #getEntityById(Long)} для получения сущности.
     *
     * @param id идентификатор сотрудника
     * @return DTO с данными сотрудника
     * @throws NotFoundException если сотрудник с указанным id не существует
     */
    @Transactional(readOnly = true)
    public StaffDto findById(Long id) {
        return staffMapper.toDto(getEntityById(id));
    }

    /**
     * Возвращает всех сотрудников.
     *
     * @return список DTO всех сотрудников;
     *         пустой список, если сотрудников нет
     */
    @Transactional(readOnly = true)
    public List<StaffDto> findAll() {
        return staffRepository.findAll()
                .stream()
                .map(staffMapper::toDto)
                .toList();
    }

    /**
     * Создаёт нового сотрудника.
     * <p>
     * Преобразует запрос в сущность, сохраняет в базу данных
     * и возвращает DTO созданной записи.
     *
     * @param request данные для создания сотрудника
     * @return DTO созданного сотрудника
     */
    @Transactional
    public StaffDto create(StaffCreateRequest request) {

        Staff staff = staffMapper.toEntity(request);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    /**
     * Обновляет данные сотрудника.
     * <p>
     * Получает существующую сущность, применяет изменения через
     * {@link StaffMapper#updateEntity(StaffUpdateRequest, Staff)} и сохраняет.
     *
     * @param id      идентификатор обновляемого сотрудника
     * @param request данные для обновления
     * @return DTO обновлённого сотрудника
     * @throws NotFoundException если сотрудник с указанным id не существует
     */
    @Transactional
    public StaffDto update(Long id, StaffUpdateRequest request) {

        Staff staff = getEntityById(id);

        staffMapper.updateEntity(request, staff);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    /**
     * Удаляет сотрудника по идентификатору.
     * <p>
     * Выполняет физическое удаление записи из базы данных.
     *
     * @param id идентификатор удаляемого сотрудника
     * @throws NotFoundException если сотрудник с указанным id не существует
     */
    @Transactional
    public void delete(Long id) {

        Staff staff = getEntityById(id);

        staffRepository.delete(staff);
    }
}