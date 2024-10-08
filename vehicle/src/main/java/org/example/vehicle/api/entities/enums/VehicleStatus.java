package org.example.vehicle.api.entities.enums;

import lombok.Getter;

@Getter
public enum VehicleStatus {

    ON_ROUTE("Đang trên hành trình"),
    ARRIVED("Đã cập bến"),
    MAINTENANCE("Đang bảo trì");

    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }
}
