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

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public Client getEntityById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Client with id %d does not exist", id)
                ));
    }

    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        return clientMapper.toDto(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Transactional
    public ClientDto create(ClientCreateRequest request) {
        Client client = clientMapper.toEntity(request);
        Client saved = clientRepository.save(client);
        return clientMapper.toDto(saved);
    }

    @Transactional
    public ClientDto update(Long id, ClientUpdateRequest request) {
        Client client = getEntityById(id);
        clientMapper.updateEntity(request, client);
        Client updated = clientRepository.save(client);
        return clientMapper.toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        Client client = getEntityById(id);
        clientRepository.delete(client);
    }
}