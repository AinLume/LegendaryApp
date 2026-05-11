package org.example.cafecrm.domain.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column
    private Integer quantity;

    @Column
    private String comment;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    @Column(nullable = false, length = 500)
    @Enumerated(EnumType.STRING)
    private Destination destination;
}
