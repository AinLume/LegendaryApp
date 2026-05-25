package org.example.cafecrm.repository;

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

    @Query(value = """
        SELECT t.table_id as id, t.number as number, t.capacity as capacity,
               t.pos_x as posX, t.pos_y as posY,
               CASE WHEN EXISTS (
                   SELECT 1 FROM reservation r
                   WHERE r.table_id = t.table_id
                   AND r.status = 'ACTIVE'
                   AND r.start_time <= :now
                   AND r.end_time > :now
               ) THEN 'OCCUPIED' ELSE 'FREE' END as status
        FROM public.restaurant_table t
        ORDER BY t.number
        """, nativeQuery = true)
    List<TableStatusProjection> findAllWithCurrentStatus(@Param("now") LocalDateTime now);

    interface TableStatusProjection {
        Integer getId();
        Integer getNumber();
        Integer getCapacity();
        Integer getPosX();
        Integer getPosY();
        String getStatus();
    }
}
