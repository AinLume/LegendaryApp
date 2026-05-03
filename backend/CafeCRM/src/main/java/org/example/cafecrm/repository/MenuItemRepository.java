package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.MenuCategory;
import org.example.cafecrm.domain.entity.MenuItem;
import org.example.cafecrm.domain.values.MenuItemType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    boolean existsByNameAndCategoryAndType(String name, MenuCategory category, MenuItemType type);
}
