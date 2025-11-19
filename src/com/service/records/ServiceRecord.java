package com.service.records;

import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.Part;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecord {

    private Customer customer;
    private Vehicle vehicle;
    private Technician technician;
    private String customerIssue;
    private String status;
    private String mechanicNotes;
    
    private double partsCost;
    private double laborCost;
    private double subtotal;
    private double discount;
    private double totalServiceCost;
    
    private List<Part> partsUsed;

    public ServiceRecord(Customer customer, Vehicle vehicle, String customerIssue) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.customerIssue = customerIssue;
        this.status = "Pending";
        this.mechanicNotes = "No notes yet.";
        this.partsUsed = new ArrayList<>();
    }

    public void setCustomerIssue(String issue) { this.customerIssue = issue; }
    public void setStatus(String status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public Technician getTechnician() { return technician; }
    public String getStatus() { return status; }
    public String getCustomerIssue() { return customerIssue; }
    public String getMechanicNotes() { return mechanicNotes; }
    public List<Part> getPartsUsed() { return partsUsed; }
    public double getTotalServiceCost() { return totalServiceCost; }

    public double getPartsCost() { return partsCost; }
    public double getLaborCost() { return laborCost; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }

    public void assignTechnician(Technician tech) {
        this.technician = tech;
        this.status = "Pending"; 
    }

    public void addMechanicNotes(String notes) {
        if (this.mechanicNotes.equals("No notes yet.")) {
            this.mechanicNotes = notes;
        } else {
            this.mechanicNotes += " | " + notes;
        }
    }

    public void addPart(Part part) {
        partsUsed.add(part);
    }

    public void calculateFinalBill() {
        double baseCost = 0;
        double laborCalc = 0;

        for (Part part : partsUsed) {
            baseCost += part.getPartCost();
            laborCalc += part.calculateLaborCost(); 
        }

        this.partsCost = baseCost;
        this.laborCost = laborCalc;
        this.subtotal = baseCost + laborCalc;
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