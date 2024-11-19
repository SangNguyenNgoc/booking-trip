package org.example.statistics.api.documents;

public enum Type {
    MONTH("tháng"),
    DAY("ngày"),
    YEAR("năm"),
    QUARTER("quý");

    private final String value;

    Type(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
