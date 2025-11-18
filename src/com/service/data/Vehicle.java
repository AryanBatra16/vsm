package com.service.data;

public class Vehicle {
    private String make;
    private String model;
    private String licensePlate;
    private boolean isUnderWarranty; // Used for P9

    public Vehicle(String make, String model, String licensePlate, boolean isUnderWarranty) {
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.isUnderWarranty = isUnderWarranty;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    
    public boolean isUnderWarranty() {
        return isUnderWarranty;
    }
}