package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.dto.table.UpdateTablePositionRequest;
import org.example.cafecrm.domain.dto.table.UpdateTableStatusRequest;
import org.example.cafecrm.domain.entity.Tables;
import org.example.cafecrm.domain.values.TableStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class TableServiceTest extends BaseServiceTest {

    @InjectMocks
    private TableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new TableService(tableRepository, tableMapper);
        setUpBaseEntities();
    }

    @Test
    void getTableById_shouldReturnTable() {
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));

        Tables result = tableService.getTableById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(tableRepository).findById(1);
    }

    @Test
    void getTableById_shouldThrowNotFoundException_whenTableNotExists() {
        when(tableRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableService.getTableById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Table with id 999 does not exist");
    }

    @Test
    void create_shouldCreateTable() {
        CreateTableRequest request = new CreateTableRequest(10, 4, 100, 200);

        when(tableRepository.existsByNumber(10)).thenReturn(false);
        when(tableMapper.toEntity(request)).thenReturn(testTable);
        when(tableRepository.save(any(Tables.class))).thenReturn(testTable);

        TableResponse mockResponse = new TableResponse(1, 10, 4, 100, 200, "FREE");
        when(tableMapper.toResponse(any(Tables.class))).thenReturn(mockResponse);

        TableResponse result = tableService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.number()).isEqualTo(10);
        assertThat(result.capacity()).isEqualTo(4);
        verify(tableRepository).existsByNumber(10);
        verify(tableRepository).save(any(Tables.class));
    }

    @Test
    void create_shouldSetFreeStatus() {
        CreateTableRequest request = new CreateTableRequest(10, 4, 100, 200);

        when(tableRepository.existsByNumber(10)).thenReturn(false);
        when(tableMapper.toEntity(request)).thenReturn(testTable);
        when(tableRepository.save(any(Tables.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TableResponse mockResponse = new TableResponse(1, 10, 4, 100, 200, "FREE");
        when(tableMapper.toResponse(any(Tables.class))).thenReturn(mockResponse);

        tableService.create(request);

        assertThat(testTable.getStatus()).isEqualTo(TableStatus.FREE);
    }

    @Test
    void create_shouldThrowConflictException_whenTableNumberExists() {
        CreateTableRequest request = new CreateTableRequest(10, 4, 100, 200);
        when(tableRepository.existsByNumber(10)).thenReturn(true);

        assertThatThrownBy(() -> tableService.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Table with number 10 already exists");

        verify(tableRepository).existsByNumber(10);
        verify(tableRepository, never()).save(any(Tables.class));
    }

    @Test
    void findAll_shouldReturnAllTables() {
        Tables table2 = createTestTable(2, 5, 6);
        Tables table3 = createTestTable(3, 6, 8);

        when(tableRepository.findAll()).thenReturn(Arrays.asList(testTable, table2, table3));

        TableResponse response1 = new TableResponse(1, 4, 4, 0, 0, "FREE");
        TableResponse response2 = new TableResponse(2, 5, 6, 0, 0, "FREE");
        TableResponse response3 = new TableResponse(3, 6, 8, 0, 0, "FREE");

        when(tableMapper.toResponse(testTable)).thenReturn(response1);
        when(tableMapper.toResponse(table2)).thenReturn(response2);
        when(tableMapper.toResponse(table3)).thenReturn(response3);

        List<TableResponse> result = tableService.findAll();

        assertThat(result).hasSize(3);
        verify(tableRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTables() {
        when(tableRepository.findAll()).thenReturn(List.of());

        List<TableResponse> result = tableService.findAll();

        assertThat(result).isEmpty();
        verify(tableRepository).findAll();
    }

    @Test
    void deleteTableById_shouldDeleteTable() {
        when(tableRepository.existsById(1)).thenReturn(true);
        doNothing().when(tableRepository).deleteById(1);

        tableService.deleteTableById(1);

        verify(tableRepository).existsById(1);
        verify(tableRepository).deleteById(1);
    }

    @Test
    void deleteTableById_shouldThrowNotFoundException_whenTableNotExists() {
        when(tableRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> tableService.deleteTableById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Table with id 999 does not exist");
    }

    @Test
    void updateTableStatusById_shouldUpdateStatus() {
        UpdateTableStatusRequest request = new UpdateTableStatusRequest(TableStatus.OCCUPIED);
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));
        when(tableRepository.save(any(Tables.class))).thenReturn(testTable);

        TableResponse mockResponse = new TableResponse(1, 4, 4, 0, 0, "OCCUPIED");
        when(tableMapper.toResponse(any(Tables.class))).thenReturn(mockResponse);

        TableResponse result = tableService.updateTableStatusById(1, request);

        assertThat(result.status()).isEqualTo("OCCUPIED");
        assertThat(testTable.getStatus()).isEqualTo(TableStatus.OCCUPIED);
        verify(tableRepository).save(any(Tables.class));
    }

    @Test
    void updateTableStatusById_shouldReturnWithoutSave_whenStatusUnchanged() {
        UpdateTableStatusRequest request = new UpdateTableStatusRequest(TableStatus.FREE);
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));

        TableResponse mockResponse = new TableResponse(1, 4, 4, 0, 0, "FREE");
        when(tableMapper.toResponse(testTable)).thenReturn(mockResponse);

        TableResponse result = tableService.updateTableStatusById(1, request);

        assertThat(result.status()).isEqualTo("FREE");
        verify(tableRepository, never()).save(any(Tables.class));
    }

    @Test
    void updateTablePosition_shouldUpdatePosition() {
        UpdateTablePositionRequest request = new UpdateTablePositionRequest(150, 250);
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));
        when(tableRepository.save(any(Tables.class))).thenReturn(testTable);

        TableResponse mockResponse = new TableResponse(1, 4, 4, 150, 250, "FREE");
        when(tableMapper.toResponse(any(Tables.class))).thenReturn(mockResponse);

        TableResponse result = tableService.updateTablePosition(1, request);

        assertThat(result.posX()).isEqualTo(150);
        assertThat(result.posY()).isEqualTo(250);
        verify(tableRepository).save(any(Tables.class));
    }

    @Test
    void updateTablePosition_shouldReturnWithoutSave_whenPositionUnchanged() {
        UpdateTablePositionRequest request = new UpdateTablePositionRequest(100, 200);
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));

        TableResponse mockResponse = new TableResponse(1, 4, 4, 100, 200, "FREE");
        lenient().when(tableMapper.toResponse(any(Tables.class))).thenReturn(mockResponse);

        TableResponse result = tableService.updateTablePosition(1, request);

        assertThat(result.posX()).isEqualTo(100);
        assertThat(result.posY()).isEqualTo(200);
        verify(tableRepository, never()).save(any(Tables.class));
    }
}
