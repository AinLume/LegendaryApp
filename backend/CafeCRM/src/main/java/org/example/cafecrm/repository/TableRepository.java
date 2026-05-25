package org.example.cafecrm.repository;

import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.entity.Tables;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<@NotNull Tables, @NotNull Integer> {

    boolean existsByNumber(Integer number);

    @Query("""
        SELECT new org.example.cafecrm.domain.dto.table.TableResponse(
            t.id,
            t.number,
            t.capacity,
            t.posX,
            t.posY,
            CASE WHEN COUNT(r.id) > 0 THEN 'OCCUPIED' ELSE 'FREE' END
        )
        FROM Tables t
        LEFT JOIN Reservation r ON r.table.id = t.id
            AND r.status = 'ACTIVE'
            AND r.startTime <= :now
            AND r.endTime > :now
        GROUP BY t.id, t.number, t.capacity, t.posX, t.posY
        ORDER BY t.number
        """)
    List<TableResponse> findAllWithCurrentStatus(@Param("now") LocalDateTime now);
}
