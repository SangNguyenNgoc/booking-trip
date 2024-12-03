package org.example.vehicle.api.repositories.specifications;

import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.enums.VehicleStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class VehicleSpecifications {

    public Specification<Vehicle> beLongToAt(String belongTo) {
        return (root, query, criteriaBuilder) -> belongTo == null ? null :
                criteriaBuilder.like(root.get("belongTo"), "%" + belongTo + "%");
    }

    public Specification<Vehicle> hasTypeId(Long typeId) {
        return (root, query, criteriaBuilder) -> typeId == null ? null :
                criteriaBuilder.equal(root.get("type").get("id"), typeId);
    }

    public Specification<Vehicle> hasStatus(VehicleStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null :
                criteriaBuilder.equal(root.get("status"), status);
    }


}
