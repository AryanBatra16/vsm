package com.service.parts;

// P5: Subclass inheriting from Part
public class BodyPart extends Part {

    public BodyPart(String partName, double partCost) {
        super(partName, partCost);
    }

    // P6: Overridden method for Polymorphism
    // Body parts have a lower labor cost (e.g., 80% of part cost)
    @Override
    public double calculateLaborCost() {
        return this.partCost * 0.8;
    }
}