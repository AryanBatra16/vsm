package com.service.parts;

public class BodyPart extends Part {
    public BodyPart(String partName, double partCost) {
        super(partName, partCost);
    }
    @Override
    public double calculateLaborCost() {
        return this.partCost * 0.20; 
    }
}