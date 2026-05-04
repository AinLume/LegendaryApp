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

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final TableService tableService;
    private final StaffService staffService;

    @Transactional(readOnly = true)
    public Reservation getEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reservation with id %d does not exist", id)));
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return reservationMapper.toResponse(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

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
}