package com.service.data;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private String make;
    private String model;
    private String licensePlate;
    private boolean isUnderWarranty;

    public Vehicle(String make, String model, String licensePlate, boolean isUnderWarranty) {
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.isUnderWarranty = isUnderWarranty;
    }

    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getLicensePlate() { return licensePlate; }
    public boolean isUnderWarranty() { return isUnderWarranty; }
}