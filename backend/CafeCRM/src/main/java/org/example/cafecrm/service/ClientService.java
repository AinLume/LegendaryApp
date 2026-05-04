package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.client.ClientCreateRequest;
import org.example.cafecrm.domain.dto.client.ClientDto;
import org.example.cafecrm.domain.dto.client.ClientUpdateRequest;
import org.example.cafecrm.domain.entity.Client;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.ClientMapper;
import org.example.cafecrm.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис управления клиентами CRM.
 * <p>
 * Предоставляет CRUD-операции для работы с сущностью {@link Client}.
 * Используется другими сервисами для получения клиента по идентификатору
 * (например, при создании доставочного заказа в {@link OrderService}).
 * <p>
 * Все операции модификации выполняются в транзакционном контексте.
 * Метод получения сущности по идентификатору ({@link #getEntityById(Long)})
 * переиспользуется остальными методами сервиса.
 *
 * @author AinLume
 * @see Client
 * @see ClientDto
 * @see OrderService
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    /**
     * Возвращает сущность клиента по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса
     * и вызываемый извне (например, {@link OrderService} при создании заказа).
     *
     * @param id идентификатор клиента
     * @return найденная сущность {@link Client}
     * @throws NotFoundException если клиент с указанным id не существует
     */
    @Transactional(readOnly = true)
    public Client getEntityById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Client with id %d does not exist", id)
                ));
    }

    /**
     * Возвращает DTO клиента по идентификатору.
     * <p>
     * Переиспользует {@link #getEntityById(Long)} для получения сущности.
     *
     * @param id идентификатор клиента
     * @return DTO с данными клиента
     * @throws NotFoundException если клиент с указанным id не существует
     */
    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        return clientMapper.toDto(getEntityById(id));
    }

    /**
     * Возвращает список всех клиентов.
     *
     * @return список DTO всех клиентов, отсортированных по умолчанию (id);
     *         пустой список, если клиентов нет
     */
    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    /**
     * Создаёт нового клиента.
     * <p>
     * Преобразует запрос в сущность, сохраняет в базу данных
     * и возвращает DTO созданного клиента.
     *
     * @param request данные для создания клиента (валидация на уровне контроллера)
     * @return DTO созданного клиента с присвоенным id
     */
    @Transactional
    public ClientDto create(ClientCreateRequest request) {
        Client client = clientMapper.toEntity(request);
        Client saved = clientRepository.save(client);
        return clientMapper.toDto(saved);
    }

    /**
     * Обновляет данные существующего клиента.
     * <p>
     * Получает сущность через {@link #getEntityById(Long)}, применяет
     * изменения через маппер и сохраняет обновлённую сущность.
     *
     * @param id      идентификатор обновляемого клиента
     * @param request данные для обновления (null-поля не применяются)
     * @return DTO обновлённого клиента
     * @throws NotFoundException если клиент с указанным id не существует
     */
    @Transactional
    public ClientDto update(Long id, ClientUpdateRequest request) {
        Client client = getEntityById(id);
        clientMapper.updateEntity(request, client);
        Client updated = clientRepository.save(client);
        return clientMapper.toDto(updated);
    }

    /**
     * Удаляет клиента по идентификатору.
     * <p>
     * Перед удалением проверяет существование клиента через
     * {@link #getEntityById(Long)}. Каскадное удаление связанных
     * заказов определяется на уровне JPA-сущности.
     *
     * @param id идентификатор удаляемого клиента
     * @throws NotFoundException если клиент с указанным id не существует
     */
    @Transactional
    public void delete(Long id) {
        Client client = getEntityById(id);
        clientRepository.delete(client);
    }
}