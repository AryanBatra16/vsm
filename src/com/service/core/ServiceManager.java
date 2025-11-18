package com.service.core;

import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.records.ServiceRecord;
import com.service.records.Technician;
import com.service.utils.FileHandler;

public class ServiceManager {

    private ServiceRecord[] pendingServices;
    private int pendingServiceCount;

    private Customer[] customers;
    private int customerCount;
    
    private Technician[] technicians;
    private FileHandler fileHandler;

    // --- NEW FIELD ---
    private Customer loggedInUser; // Tracks who is using the system

    public ServiceManager() {
        this.pendingServices = new ServiceRecord[100];
        this.pendingServiceCount = 0;
        this.customers = new Customer[100];
        this.customerCount = 0;
        this.fileHandler = new FileHandler();
        
        this.technicians = new Technician[2];
        this.technicians[0] = new Technician("Bob", "Engines");
        this.technicians[1] = new Technician("Alice", "Bodywork");
        
        // --- UPDATED ---
        // Create a default admin user for testing
        // Login with phone: "admin", password: "password"
        Customer adminUser = new Customer("Admin Staff", "admin", "password", false);
        adminUser.setIsAdmin(true); // Make this user an admin
        this.customers[customerCount++] = adminUser; // Add admin to the list

        // Create a default regular customer for testing
        // Login with phone: "555-1234", password: "pass"
        this.customers[customerCount++] = new Customer("John Doe", "555-1234", "pass", true);
    }

    // --- NEW METHOD: LOGIN ---
    public boolean login(String phone, String password) {
        Customer user = findCustomer(phone);
        if (user != null && user.checkPassword(password)) {
            this.loggedInUser = user; // Set the logged in user
            return true; // Login successful
        }
        return false; // Login failed
    }

    // --- NEW METHOD: LOGOUT ---
    public void logout() {
        this.loggedInUser = null; // Clear the user session
    }

    // --- NEW METHOD: GET LOGGED IN USER ---
    public Customer getLoggedInUser() {
        return this.loggedInUser;
    }

    // --- NEW METHOD: REGISTER ---
    public boolean registerCustomer(String name, String phone, String password) {
        if (findCustomer(phone) != null) {
            System.out.println("Error: A user with this phone number already exists.");
            return false;
        }
        
        Customer newCustomer = new Customer(name, phone, password, false);
        if (customerCount < customers.length) {
            customers[customerCount++] = newCustomer;
            return true;
        } else {
            System.out.println("Error: Customer database is full.");
            return false;
        }
    }

    public Customer findCustomer(String phone) {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getPhone().equals(phone)) {
                return customers[i];
            }
        }
        return null;
    }

    // This method is now only for creating a customer *during* booking
    // We will keep it for the admin to use.
    public Customer createCustomer(String name, String phone, boolean isLoyalty) {
        Customer newCustomer = new Customer(name, phone, isLoyalty);
        if (customerCount < customers.length) {
            customers[customerCount++] = newCustomer;
            return newCustomer;
        }
        return null; // Array is full
    }

    // This method is fine as-is. It will be used by Admins and Customers.
    public void bookService(Customer customer, Vehicle vehicle, String issue) {
        ServiceRecord newRecord = new ServiceRecord(customer, vehicle, issue);
        
        Technician tech = (issue.toLowerCase().contains("engine")) ? technicians[0] : technicians[1];
        newRecord.assignTechnician(tech);
        
        if (pendingServiceCount < pendingServices.length) {
            pendingServices[pendingServiceCount++] = newRecord;
            System.out.println("Service booked successfully for " + vehicle.getLicensePlate());
        } else {
            System.out.println("Error: Service center is full.");
        }
    }

    // This is the ADMIN version (shows all services)
    public void listAllPendingServices() {
        System.out.println("\n--- All Pending Service Jobs ---");
        if (pendingServiceCount == 0) {
            System.out.println("No pending services found.");
            return;
        }
        for (int i = 0; i < pendingServiceCount; i++) {
            ServiceRecord r = pendingServices[i];
            System.out.println(i + ": " + r.getVehicle().getLicensePlate() + 
                               " (" + r.getCustomer().getName() + ") - Status: " + r.getStatus());
        }
        System.out.println("----------------------------");
    }

    // --- NEW METHOD: CUSTOMER VERSION ---
    // Shows only services for a specific customer
    public void listMyPendingServices(Customer customer) {
        System.out.println("\n--- Your Pending Service Jobs ---");
        int count = 0;
        for (int i = 0; i < pendingServiceCount; i++) {
            if (pendingServices[i].getCustomer() == customer) {
                ServiceRecord r = pendingServices[i];
                System.out.println(i + ": " + r.getVehicle().getLicensePlate() + 
                                   " (" + r.getVehicle().getModel() + ") - Status: " + r.getStatus());
                count++;
            }
        }
        if (count == 0) {
            System.out.println("You have no pending services.");
        }
        System.out.println("----------------------------");
    }


    public ServiceRecord getPendingService(int index) {
        if (index >= 0 && index < pendingServiceCount) {
            return pendingServices[index];
        }
        return null;
    }

    public void completeAndBillService(int serviceIndex) {
        ServiceRecord record = getPendingService(serviceIndex);
        if (record == null) {
            System.out.println("Invalid service index.");
            return;
        }

        record.calculateFinalBill();
        
        System.out.println("\n--- Final Bill for " + record.getVehicle().getLicensePlate() + " ---");
        System.out.println("  Parts:    $" + record.getPartsCost());
        System.out.println("  Labor:    $" + record.getLaborCost());
        System.out.println("  Subtotal: $" + record.getSubtotal());
        System.out.println("  Discount: -$" + record.getDiscount());
        System.out.println("  --------------------");
        System.out.println("  TOTAL:    $" + record.getTotalServiceCost());
        
        fileHandler.saveServiceLog(record);

        for (int i = serviceIndex; i < pendingServiceCount - 1; i++) {
            pendingServices[i] = pendingServices[i + 1];
        }
        pendingServices[pendingServiceCount - 1] = null;
        pendingServiceCount--;
    }

    public void loadLogs() {
        fileHandler.loadAllServiceLogs();
    }
}