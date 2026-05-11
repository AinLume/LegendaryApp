package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.client.ClientCreateRequest;
import org.example.cafecrm.domain.dto.client.ClientDto;
import org.example.cafecrm.domain.dto.client.ClientUpdateRequest;
import org.example.cafecrm.domain.entity.Client;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ClientServiceTest extends BaseServiceTest {

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, clientMapper, passwordEncoder);
        setUpBaseEntities();
    }

    @Test
    void getEntityById_shouldReturnClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        Client result = clientService.getEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Client");
        verify(clientRepository).findById(1L);
    }

    @Test
    void getEntityById_shouldThrowNotFoundException_whenClientNotExists() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client with id 999 does not exist");
    }

    @Test
    void findById_shouldReturnClientDto() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        ClientDto dto = new ClientDto(1L, "Test Client", "+79001234567", "test@example.com", "Test Address");
        when(clientMapper.toDto(testClient)).thenReturn(dto);

        ClientDto result = clientService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Client");
        verify(clientRepository).findById(1L);
        verify(clientMapper).toDto(testClient);
    }

    @Test
    void findByPhoneNumber_shouldReturnClient() {
        when(clientRepository.findByPhone("+79001234567")).thenReturn(testClient);

        Client result = clientService.findByPhoneNumber("+79001234567");

        assertThat(result).isNotNull();
        assertThat(result.getPhone()).isEqualTo("+79001234567");
        verify(clientRepository).findByPhone("+79001234567");
    }

    @Test
    void findByPhoneNumber_shouldReturnNull_whenClientNotExists() {
        when(clientRepository.findByPhone("+79999999999")).thenReturn(null);

        Client result = clientService.findByPhoneNumber("+79999999999");

        assertThat(result).isNull();
        verify(clientRepository).findByPhone("+79999999999");
    }

    @Test
    void findAll_shouldReturnAllClients() {
        Client client2 = createTestClient(2L, "Client 2", "+79001234568");
        Client client3 = createTestClient(3L, "Client 3", "+79001234569");

        List<Client> clients = Arrays.asList(testClient, client2, client3);
        when(clientRepository.findAll()).thenReturn(clients);

        ClientDto dto1 = new ClientDto(1L, "Test Client", "+79001234567", "test@example.com", "Test Address");
        ClientDto dto2 = new ClientDto(2L, "Client 2", "+79001234568", "test@example.com", "Test Address");
        ClientDto dto3 = new ClientDto(3L, "Client 3", "+79001234569", "test@example.com", "Test Address");

        when(clientMapper.toDto(testClient)).thenReturn(dto1);
        when(clientMapper.toDto(client2)).thenReturn(dto2);
        when(clientMapper.toDto(client3)).thenReturn(dto3);

        List<ClientDto> result = clientService.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(2).id()).isEqualTo(3L);

        verify(clientRepository).findAll();
        verify(clientMapper, times(3)).toDto(any(Client.class));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoClients() {
        when(clientRepository.findAll()).thenReturn(List.of());

        List<ClientDto> result = clientService.findAll();

        assertThat(result).isEmpty();
        verify(clientRepository).findAll();
    }

    @Test
    void create_shouldCreateClient() {
        ClientCreateRequest request = new ClientCreateRequest(
                "New Client",
                "+79001234570",
                "new@example.com",
                "New Address",
                "password"
        );

        Client clientToSave = createTestClient(null, "New Client", "+79001234570");
        when(clientMapper.toEntity(request)).thenReturn(clientToSave);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$encodedPassword");

        Client savedClient = createTestClient(1L, "New Client", "+79001234570");
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        ClientDto expectedDto = new ClientDto(1L, "New Client", "+79001234570", "new@example.com", "New Address");
        when(clientMapper.toDto(savedClient)).thenReturn(expectedDto);

        ClientDto result = clientService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New Client");
        assertThat(result.phone()).isEqualTo("+79001234570");

        verify(clientMapper).toEntity(request);
        verify(passwordEncoder).encode("password");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_shouldUpdateClient() {
        ClientUpdateRequest request = new ClientUpdateRequest(
                "Updated Name",
                "+79001234571",
                "updated@example.com",
                "Updated Address",
                null
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client updatedClient = createTestClient(1L, "Updated Name", "+79001234571");
        ClientDto expectedDto = new ClientDto(1L, "Updated Name", "+79001234571", "updated@example.com", "Updated Address");
        when(clientMapper.toDto(any(Client.class))).thenReturn(expectedDto);

        ClientDto result = clientService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated Name");
        assertThat(result.phone()).isEqualTo("+79001234571");

        verify(clientRepository).findById(1L);
        verify(clientMapper).updateEntity(request, testClient);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_shouldThrowNotFoundException_whenClientNotExists() {
        ClientUpdateRequest request = new ClientUpdateRequest(
                "Updated Name",
                "+79001234571",
                "updated@example.com",
                "Updated Address",
                null
        );

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.update(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client with id 999 does not exist");

        verify(clientRepository).findById(999L);
        verify(clientMapper, never()).updateEntity(any(), any());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void delete_shouldDeleteClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        doNothing().when(clientRepository).delete(testClient);

        clientService.delete(1L);

        verify(clientRepository).findById(1L);
        verify(clientRepository).delete(testClient);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenClientNotExists() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client with id 999 does not exist");

        verify(clientRepository).findById(999L);
        verify(clientRepository, never()).delete(any(Client.class));
    }
}
