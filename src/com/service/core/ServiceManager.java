package com.service.core;

import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.Part;
import com.service.records.ServiceRecord;
import com.service.records.Technician;
import com.service.utils.FileHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceManager {

    private List<ServiceRecord> pendingServices;
    private List<Customer> customers;
    private Technician[] technicians;
    private FileHandler fileHandler;
    private Customer loggedInUser; 

    public ServiceManager() {
        this.pendingServices = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.fileHandler = new FileHandler();
        
        this.technicians = new Technician[2];
        this.technicians[0] = new Technician("Bob", "Mechanical Systems");
        this.technicians[1] = new Technician("Alice", "Bodywork");
        
        this.customers = fileHandler.loadUsers();
        this.pendingServices = fileHandler.loadPendingServices();

        if (this.customers.isEmpty()) {
            Customer adminUser = new Customer("System Admin", "admin", "1234", true, 0);
            this.customers.add(adminUser);
            fileHandler.saveUsers(this.customers); 
        }
    }

    public boolean login(String phone, String password) {
        Customer user = findCustomer(phone);
        if (user != null && user.checkPassword(password)) {
            this.loggedInUser = user;
            return true;
        }
        return false;
    }

    public void logout() { this.loggedInUser = null; }
    public Customer getLoggedInUser() { return this.loggedInUser; }

    public boolean registerCustomer(String name, String phone, String password) {
        if (findCustomer(phone) != null) return false;
        customers.add(new Customer(name, phone, password, false, 0));
        fileHandler.saveUsers(customers); 
        return true;
    }

    public Customer findCustomer(String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone)) return c;
        }
        return null;
    }

    public Customer createCustomer(String name, String phone) {
        Customer newCustomer = new Customer(name, phone, "12345", false, 0);
        customers.add(newCustomer);
        fileHandler.saveUsers(customers);
        return newCustomer;
    }

    public void bookService(Customer customer, Vehicle vehicle, String issue, List<Part> initialParts, String notes) {
        ServiceRecord newRecord = new ServiceRecord(customer, vehicle, issue);
        
        Technician tech = (issue.toLowerCase().contains("mechanical")) ? technicians[0] : technicians[1];
        newRecord.assignTechnician(tech);
        
        if (notes != null && !notes.isEmpty()) {
            newRecord.addMechanicNotes(notes);
        }
        
        for(Part p : initialParts) {
            newRecord.addPart(p);
        }
        
        pendingServices.add(newRecord);
        customer.incrementServiceCount();
        fileHandler.saveUsers(customers);
        fileHandler.savePendingServices(pendingServices); 
        
        System.out.println("Service booked successfully for " + vehicle.getLicensePlate());
    }

    public void removeService(int index) {
        int realIndex = index - 1;
        if (realIndex >= 0 && realIndex < pendingServices.size()) {
            ServiceRecord r = pendingServices.remove(realIndex);
            fileHandler.savePendingServices(pendingServices);
            System.out.println("Removed service for " + r.getVehicle().getLicensePlate());
        } else {
            System.out.println("Invalid index.");
        }
    }

    public void listAllPendingServices() {
        System.out.println("\n--- All Pending Service Jobs ---");
        if (pendingServices.isEmpty()) {
            System.out.println("No pending services found.");
            return;
        }
        for (int i = 0; i < pendingServices.size(); i++) {
            ServiceRecord r = pendingServices.get(i);
            System.out.println((i + 1) + ": " + r.getVehicle().getLicensePlate() + 
                               " (" + r.getCustomer().getName() + ") - Status: " + r.getStatus());
        }
        System.out.println("----------------------------");
    }

    public void listMyStatus(Customer customer) {
        System.out.println("\n--- Service Status for " + customer.getName() + " ---");
        int count = 0;
        for (ServiceRecord r : pendingServices) {
            if (r.getCustomer().getPhone().equals(customer.getPhone())) {
                System.out.println("Vehicle: " + r.getVehicle().getLicensePlate());
                System.out.println("Status: " + r.getStatus());
                System.out.println("Issues: " + r.getCustomerIssue());
                System.out.println("--------------------");
                count++;
            }
        }
        if (count == 0) System.out.println("You have no services currently in progress.");
        System.out.println("----------------------------");
    }
    
    public void viewMyBill(Customer customer) {
        System.out.println("\n--- Your Bills ---");
        boolean found = false;
        for (ServiceRecord r : pendingServices) {
            if (r.getCustomer().getPhone().equals(customer.getPhone())) {
                System.out.println("Vehicle: " + r.getVehicle().getLicensePlate());
                System.out.println("Service in progress. Your bill will be available when it is completed.");
                found = true;
            }
        }
        if (!found) System.out.println("No active services.");
    }

    public ServiceRecord getPendingService(int index) {
        int realIndex = index - 1;
        if (realIndex >= 0 && realIndex < pendingServices.size()) {
            return pendingServices.get(realIndex);
        }
        return null;
    }

    public void completeAndBillService(int serviceIndex, Scanner scanner) {
        ServiceRecord record = getPendingService(serviceIndex);
        if (record == null) {
            System.out.println("Invalid service index.");
            return;
        }

        System.out.println("\n--- Pricing Parts for " + record.getVehicle().getLicensePlate() + " ---");
        for (Part p : record.getPartsUsed()) {
            if (p.getPartCost() == 0) {
                while (true) {
                    System.out.print("Enter price for '" + p.getPartName() + "': Rs. ");
                    try {
                        double cost = Double.parseDouble(scanner.nextLine());
                        if (cost >= 0) {
                            p.setPartCost(cost);
                            break;
                        }
                        System.out.println("Price cannot be negative.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                }
            }
        }

        record.calculateFinalBill(); // This sets status to Completed
        
        System.out.println("\n==========================================");
        System.out.println("             FINAL INVOICE                ");
        System.out.println("==========================================");
        System.out.println("Customer: " + record.getCustomer().getName());
        System.out.println("Vehicle:  " + record.getVehicle().getMake() + " " + record.getVehicle().getModel());
        System.out.println("------------------------------------------");
        System.out.printf("Parts Total:     Rs. %.2f%n", record.getPartsCost());
        System.out.printf("Labor Total:     Rs. %.2f%n", record.getLaborCost());
        System.out.printf("Subtotal:        Rs. %.2f%n", record.getSubtotal());
        if(record.getDiscount() > 0) {
            System.out.printf("Discount Applied: -Rs. %.2f%n", record.getDiscount());
        }
        System.out.println("------------------------------------------");
        System.out.printf("GRAND TOTAL (Tax Inclusive): Rs. %.2f%n", record.getTotalServiceCost());
        System.out.println("==========================================");
        
        fileHandler.saveServiceLog(record);
        pendingServices.remove(serviceIndex - 1);
        fileHandler.savePendingServices(pendingServices); 
    }

    public void loadLogs() {
        fileHandler.loadAllServiceLogs();
    }
    
    public void updateCustomerData() {
        fileHandler.saveUsers(customers);
    }
}