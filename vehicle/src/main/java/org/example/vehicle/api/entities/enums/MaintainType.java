package org.example.vehicle.api.entities.enums;

import lombok.Getter;

@Getter
public enum MaintainType {

    PERIODIC("Bảo trì định kỳ"),
    EMERGENCY("Bảo trì khẩn cấp"),
    INSPECTION("Kiểm tra định kỳ");

    private final String value;

    MaintainType(String value) {this.value = value;}
}
