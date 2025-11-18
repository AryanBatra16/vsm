package com.service.parts;

// P5: Subclass inheriting from Part
public class EnginePart extends Part {

    public EnginePart(String partName, double partCost) {
        super(partName, partCost);
    }

    // P6: Overridden method for Polymorphism
    // Engine parts have a higher labor cost (e.g., 150% of part cost)
    @Override
    public double calculateLaborCost() {
        return this.partCost * 1.5;
    }
}