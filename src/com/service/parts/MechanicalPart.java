package com.service.parts;

public class MechanicalPart extends Part {

    public MechanicalPart(String partName, double partCost) {
        super(partName, partCost);
    }

    @Override
    public double calculateLaborCost() {
        return this.partCost * 0.30; // 30% Labor
    }
}