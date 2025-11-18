package com.service.records;

import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.Part;

public class ServiceRecord {

    private Customer customer;
    private Vehicle vehicle;
    private Technician technician;
    private String customerIssue;
    private String status;

    private String mechanicNotes;
    private double totalServiceCost;

    private double partsCost;
    private double laborCost;
    private double subtotal;
    private double discount;
    
    private Part[] partsUsed;
    private int partCount;

    public ServiceRecord(Customer customer, Vehicle vehicle, String customerIssue) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.customerIssue = customerIssue;
        this.status = "Pending";
        this.mechanicNotes = "No notes yet.";
        this.totalServiceCost = 0;
        this.partsUsed = new Part[20];
        this.partCount = 0;

        this.partsCost = 0;
        this.laborCost = 0;
        this.subtotal = 0;
        this.discount = 0;
    }

    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public Technician getTechnician() { return technician; }
    public String getStatus() { return status; }
    public String getCustomerIssue() { return customerIssue; }
    public String getMechanicNotes() { return mechanicNotes; }
    public Part[] getPartsUsed() { return partsUsed; }
    public int getPartCount() { return partCount; }
    public double getTotalServiceCost() { return totalServiceCost; }

    public double getPartsCost() { return partsCost; }
    public double getLaborCost() { return laborCost; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }

    public void assignTechnician(Technician tech) {
        this.technician = tech;
        this.status = "In-Progress";
    }

    public void addMechanicNotes(String notes) {
        if (this.mechanicNotes.equals("No notes yet.")) {
            this.mechanicNotes = notes;
        } else {
            this.mechanicNotes += " | " + notes;
        }
    }

    public void addPart(Part part) {
        if (partCount < partsUsed.length) {
            partsUsed[partCount] = part;
            partCount++;
        } else {
            System.out.println("Error: Maximum parts for this service reached.");
        }
    }

    public void calculateFinalBill() {
        double baseCost = 0;
        double laborCost = 0;

        for (int i = 0; i < partCount; i++) {
            Part part = partsUsed[i];
            baseCost += part.getPartCost();
            laborCost += part.calculateLaborCost(); 
        }

        this.partsCost = baseCost;
        this.laborCost = laborCost;
        this.subtotal = baseCost + laborCost;
        this.discount = 0; 

        if (vehicle.isUnderWarranty()) {
            this.discount = this.subtotal * 0.5;
            addMechanicNotes("Applied 50% warranty discount.");
        } else if (customer.isLoyaltyMember()) {
            this.discount = this.subtotal * 0.1;
            addMechanicNotes("Applied 10% loyalty discount.");
        }

        this.totalServiceCost = this.subtotal - this.discount;
        this.status = "Completed";
    }
}