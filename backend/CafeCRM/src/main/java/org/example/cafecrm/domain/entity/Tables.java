package org.example.cafecrm.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cafecrm.domain.values.TableStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "table_id")
    private Integer id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "pos_x")
    private Integer posX;

    @Column(name = "pos_y")
    private Integer posY;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TableStatus status;

//    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
//    private List<Order> orders = new ArrayList<>();
//
    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
}