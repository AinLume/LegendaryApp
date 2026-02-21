package org.example.cafecrm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cafecrm.enums.MenuItemType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "menu_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id", nullable = false)
    private MenuCategory category;

    @Column(nullable = false, length = 50)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private MenuItemType type;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
