package com.example.admin;

public class ScannedData {
    private double value;
    private String dateTime;

    public ScannedData() {
        // Пустой конструктор требуется для Firebase
    }

    public ScannedData(double value, String dateTime) {
        this.value = value;
        this.dateTime = dateTime;
    }

    public double getValue() {
        return value;
    }

    public String getDateTime() {
        return dateTime;
    }
}
