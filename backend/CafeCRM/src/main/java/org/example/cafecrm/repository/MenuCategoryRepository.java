package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.MenuCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<@NotNull MenuCategory, @NotNull Integer> {

    @EntityGraph(attributePaths = "items")
    @Query("select mc from MenuCategory mc where mc.id =: categoryId")
    MenuCategory findAllItemsByCategoryId(@PathVariable("categoryId") @NotNull Integer categoryId);

    @EntityGraph(attributePaths = "items")
    @Query("select mc from MenuCategory mc")
    List<MenuCategory> findAllCategoriesWithItems();

    boolean existsByName(String name);
}
