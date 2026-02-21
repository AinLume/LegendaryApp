package org.example.cafecrm.repository;

import org.example.cafecrm.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Integer> {
}
