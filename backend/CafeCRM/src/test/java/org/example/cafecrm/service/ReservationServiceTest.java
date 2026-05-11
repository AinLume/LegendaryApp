package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.reservation.CreateReservationRequest;
import org.example.cafecrm.domain.dto.reservation.ReservationResponse;
import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.values.ReservationStatus;
import org.example.cafecrm.domain.values.ReservationType;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ReservationServiceTest extends BaseServiceTest {

    @Mock
    private TableService tableService;

    @Mock
    private StaffService staffService;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, reservationMapper, tableService, staffService);
        setUpBaseEntities();
    }

    @Test
    void getEntityById_shouldReturnReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        var result = reservationService.getEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(reservationRepository).findById(1L);
    }

    @Test
    void getEntityById_shouldThrowNotFoundException_whenReservationNotExists() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reservation with id 999 does not exist");
    }

    @Test
    void getById_shouldReturnReservationResponse() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        var response = mock(org.example.cafecrm.domain.dto.reservation.ReservationResponse.class);
        when(reservationMapper.toResponse(testReservation)).thenReturn(response);

        var result = reservationService.getById(1L);

        assertThat(result).isNotNull();
        verify(reservationRepository).findById(1L);
        verify(reservationMapper).toResponse(testReservation);
    }

    @Test
    void getAll_shouldReturnAllReservations() {
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(testReservation));

        var response = mock(org.example.cafecrm.domain.dto.reservation.ReservationResponse.class);
        when(reservationMapper.toResponse(testReservation)).thenReturn(response);

        List<org.example.cafecrm.domain.dto.reservation.ReservationResponse> result = reservationService.getAll();

        assertThat(result).hasSize(1);
        verify(reservationRepository).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<ReservationResponse> result = reservationService.getAll();

        assertThat(result).isEmpty();
        verify(reservationRepository).findAll();
    }

    @Test
    void getAvailableTables_shouldReturnAvailableTables() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);

        TableResponse table1 = new TableResponse(1, 1, 4, 0, 0, "FREE");
        TableResponse table2 = new TableResponse(2, 2, 6, 0, 0, "FREE");

        when(tableService.findAll()).thenReturn(Arrays.asList(table1, table2));
        when(reservationRepository.existsConflictingReservation(1, startTime, endTime)).thenReturn(false);
        when(reservationRepository.existsConflictingReservation(2, startTime, endTime)).thenReturn(false);

        List<TableShortResponse> result = reservationService.getAvailableTables(startTime, endTime, 4);

        assertThat(result).hasSize(2);
        verify(tableService).findAll();
    }

    @Test
    void getAvailableTables_shouldFilterByCapacity() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);

        TableResponse table1 = new TableResponse(1, 1, 2, 0, 0, "FREE");
        TableResponse table2 = new TableResponse(2, 2, 6, 0, 0, "FREE");

        when(tableService.findAll()).thenReturn(Arrays.asList(table1, table2));
        when(reservationRepository.existsConflictingReservation(1, startTime, endTime)).thenReturn(false);
        when(reservationRepository.existsConflictingReservation(2, startTime, endTime)).thenReturn(false);

        List<TableShortResponse> result = reservationService.getAvailableTables(startTime, endTime, 4);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).tableId()).isEqualTo(2);
    }

    @Test
    void getAvailableTables_shouldThrowConflictException_whenStartTimeAfterEndTime() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        assertThatThrownBy(() -> reservationService.getAvailableTables(startTime, endTime, 4))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Start time must be before end time");

        verify(tableService, never()).findAll();
    }

    @Test
    void create_shouldCreateReservation() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);

        CreateReservationRequest request = new CreateReservationRequest(
                1, "Guest Name", "+79001234567", 4, startTime, endTime, ReservationType.TABLE, null
        );

        when(tableService.getTableById(1)).thenReturn(testTable);
        when(reservationRepository.existsConflictingReservation(1, startTime, endTime)).thenReturn(false);
        when(staffService.getEntityById(1L)).thenReturn(testStaff);

        when(reservationMapper.toEntity(request)).thenReturn(testReservation);
        when(reservationRepository.save(any())).thenReturn(testReservation);

        var response = mock(org.example.cafecrm.domain.dto.reservation.ReservationResponse.class);
        when(reservationMapper.toResponse(testReservation)).thenReturn(response);

        var result = reservationService.create(request, 1L);

        assertThat(result).isNotNull();

        verify(tableService).getTableById(1);
        verify(reservationRepository).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenStartTimeAfterEndTime() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(4);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        CreateReservationRequest request = new CreateReservationRequest(
                1, "Guest", "+79001234567", 4, startTime, endTime, ReservationType.TABLE, null
        );

        assertThatThrownBy(() -> reservationService.create(request, 1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Start time must be before end time");

        verify(tableService, never()).getTableById(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenTableCapacityInsufficient() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);

        CreateReservationRequest request = new CreateReservationRequest(
                1, "Guest", "+79001234567", 10, startTime, endTime, ReservationType.TABLE, null
        );

        when(tableService.getTableById(1)).thenReturn(testTable);

        assertThatThrownBy(() -> reservationService.create(request, 1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Table capacity");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenTableAlreadyReserved() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);

        CreateReservationRequest request = new CreateReservationRequest(
                1, "Guest", "+79001234567", 4, startTime, endTime, ReservationType.TABLE, null
        );

        when(tableService.getTableById(1)).thenReturn(testTable);
        when(reservationRepository.existsConflictingReservation(1, startTime, endTime)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.create(request, 1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already reserved");

        verify(staffService, never()).getEntityById(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancel_shouldCancelReservation() {
        testReservation.setStatus(ReservationStatus.ACTIVE);
        testReservation.setEndTime(LocalDateTime.now().plusHours(1));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any())).thenReturn(testReservation);

        var response = mock(org.example.cafecrm.domain.dto.reservation.ReservationResponse.class);
        when(reservationMapper.toResponse(testReservation)).thenReturn(response);

        var result = reservationService.cancel(1L);

        assertThat(result).isNotNull();
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);

        verify(reservationRepository).save(any());
    }

    @Test
    void cancel_shouldThrowConflictException_whenAlreadyCancelled() {
        testReservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        assertThatThrownBy(() -> reservationService.cancel(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already cancelled");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancel_shouldThrowConflictException_whenPastReservation() {
        testReservation.setStatus(ReservationStatus.ACTIVE);
        testReservation.setEndTime(LocalDateTime.now().minusHours(1));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        assertThatThrownBy(() -> reservationService.cancel(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot cancel past reservation");

        verify(reservationRepository, never()).save(any());
    }
}
