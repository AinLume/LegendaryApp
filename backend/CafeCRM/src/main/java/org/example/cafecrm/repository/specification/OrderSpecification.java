package org.example.cafecrm.repository.specification;

import org.example.cafecrm.domain.entity.Order;
import org.example.cafecrm.domain.values.OrderStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<@NotNull Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<@NotNull Order> hasClientId(Long clientId) {
        return (root, query, cb) -> clientId == null
                ? cb.conjunction()
                : cb.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<@NotNull Order> hasTableId(Integer tableId) {
        return (root, query, cb) -> tableId == null
                ? cb.conjunction()
                : cb.equal(root.get("table").get("id"), tableId);
    }
}
