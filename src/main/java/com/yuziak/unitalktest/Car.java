package com.yuziak.unitalktest;

public enum Car {
    FERRARI, BMW, AUDI, HONDA;

    public static boolean isValid(String auto) {
        for (Car car : values()) {
            if (car.name().equalsIgnoreCase(auto)) {
                return true;
            }
        }
        return false;
    }

    public static Car fromString(String auto) {
        for (Car car : values()) {
            if (car.name().equalsIgnoreCase(auto)) {
                return car;
            }
        }
        throw new IllegalArgumentException("Invalid car: " + auto);
    }
}
