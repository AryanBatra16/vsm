package com.service.parts;

// P5: Base Part class for Inheritance
public abstract class Part {
    protected String partName;
    protected double partCost;

    public Part(String partName, double partCost) {
        this.partName = partName;
        this.partCost = partCost;
    }

    public String getPartName() {
        return partName;
    }

    public double getPartCost() {
        return partCost;
    }

    // P6: Method to be overridden (Polymorphism)
    public abstract double calculateLaborCost();
}