package com.service.parts;

import java.io.Serializable;

public abstract class Part implements Serializable {
    protected String partName;
    protected double partCost;

    public Part(String partName, double partCost) {
        this.partName = partName;
        this.partCost = partCost;
    }

    public String getPartName() { return partName; }
    public double getPartCost() { return partCost; }

    public void setPartCost(double partCost) {
        this.partCost = partCost;
    }

    public abstract double calculateLaborCost();
}