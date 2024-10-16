package org.example.vehicle.api.entities.enums;

import lombok.Getter;
import org.example.vehicle.utils.exception.InputInvalidException;

import java.util.List;

@Getter
public enum VehicleStatus {

    ON_ROUTE("ON_ROUTE"),
    ARRIVED("ARRIVED"),
    MAINTENANCE("MAINTENANCE"),;

    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }

    public static VehicleStatus getVehicleStatus(String value) {
        for (VehicleStatus vehicleStatus : VehicleStatus.values()) {
            if (vehicleStatus.value.equals(value)) {
                return vehicleStatus;
            }
        }
        throw new InputInvalidException(List.of("Vehicle status not found: ", value));
    }
}
