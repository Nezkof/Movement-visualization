package com.example.movement_visualisation.enums;

public enum WarningCodes {
    FIELD_WIDTH_TOO_HIGH(101, "Значення ширини поля зменшено до оптимального (%d)"),
    FIELD_WIDTH_TOO_LOW(102, "Значення ширини поля збільшено до оптимального (%d)"),
    FIELD_HEIGHT_TOO_HIGH(201, "Значення висоти поля зменшено до оптимального (%d)"),
    FIELD_HEIGHT_TOO_LOW(202, "Значення висоти поля збільшено до оптимального (%d)"),
    OBJECTS_NUMBER_TOO_HIGH(301, "Значення кількості об'єктів зменшено до оптимального (%d)"),
    OBJECTS_NUMBER_TOO_LOW(302, "Значення кількості об'єктів збільшено до оптимального (%d)");

    private final int code;
    private final String message;

    WarningCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getMessage(int value) {
        return String.format(message, value);
    }
}
