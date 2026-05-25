package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Reservation;
import org.example.cafecrm.domain.entity.Tables;
import org.example.cafecrm.domain.values.ReservationStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<@NotNull Reservation, @NotNull Long> {

    /**
     * Находит активные бронирования для стола, пересекающиеся по времени
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.table.id = :tableId
        AND r.status = 'ACTIVE'
        AND r.startTime < :endTime
        AND r.endTime > :startTime
        """)
    List<Reservation> findConflictingReservations(
            @Param("tableId") Integer tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Проверяет, есть ли активные бронирования для стола в указанное время
     */
    default boolean existsConflictingReservation(Integer tableId, LocalDateTime startTime, LocalDateTime endTime) {
        return !findConflictingReservations(tableId, startTime, endTime).isEmpty();
    }

    List<Reservation> findAllByStatusOrderByStartTimeAsc(ReservationStatus status);

    List<Reservation> findByTableIdOrderByStartTimeAsc(Long tableId);

}
