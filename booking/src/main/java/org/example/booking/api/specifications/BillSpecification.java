package org.example.booking.api.specifications;

import jakarta.persistence.criteria.*;
import org.example.booking.api.entities.Bill;
import org.example.booking.api.entities.Trip;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class BillSpecification {

    public static Specification<Bill> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (Root<Bill> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("createDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createDate"), from);
            } else {
                return cb.lessThanOrEqualTo(root.get("createDate"), to);
            }
        };
    }
    public static Specification<Bill> hasStatus(Integer statusId){
        return (Root<Bill> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                statusId == null ? null : cb.like(root.get("id"), "%" + statusId + "%");
    }

    public static Specification<Bill> hasPhoneNumber(String phoneNumber){
        return (Root<Bill> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                phoneNumber == null ? null : cb.like(root.get("passengerPhone"), "%" + phoneNumber + "%");
    }

    public static Specification<Bill> parentIsNull(){
        return (Root<Bill> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.isNull(root.get("parent"));
    }

    public static Specification<Bill> hasTripStartTimeBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) -> {
            if (from == null && to == null) {
                return null;
            }

            Join<Bill, Trip> tripJoin = root.join("trip", JoinType.INNER);

            if (from != null && to != null) {
                return criteriaBuilder.between(tripJoin.get("startTime"), from, to);
            } else if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(tripJoin.get("startTime"), from);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(tripJoin.get("startTime"), to);
            }
        };
    }
}
