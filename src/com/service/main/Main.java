package com.service.main;

import com.service.core.ServiceManager;
import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.BodyPart;
import com.service.parts.MechanicalPart;
import com.service.parts.Part;
import com.service.records.ServiceRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static ServiceManager manager = new ServiceManager();
    
    private static final String PHONE_REGEX = "^\\d{10}$"; 
    private static final String LICENSE_PLATE_REGEX = "^[A-Z]{2}\\d[A-Z]\\d{4}$";

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Welcome to the Vehicle Service System ---");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Customer");
            System.out.println("3. Register as New Customer");
            System.out.println("4. Exit");
            
            int choice = getIntInput("Enter choice (1-4): ", 1, 4);

            switch (choice) {
                case 1: runAdminLogin(); break;
                case 2: runCustomerLogin(); break;
                case 3: runRegistration(); break;
                case 4: 
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    if (choice == 0) running = false;
            }
        }
        scanner.close();
    }

    // --- LOGIN FLOWS ---

    private static void runAdminLogin() {
        System.out.println("\n--- Admin Login ---");
        System.out.println("0. Back");
        String username = getStringInput("Enter username: ");
        if(username.equals("0")) return;

        String password = getStringInput("Enter password: ");

        if (username.equals("admin") && password.equals("1234")) {
            if(manager.login(username, password)) {
                System.out.println("Welcome, Admin!");
                runAdminMenu();
            } else {
                System.out.println("System Error: Admin account missing.");
            }
        } else {
            System.out.println("Invalid Admin credentials.");
        }
    }

    private static void runCustomerLogin() {
        System.out.println("\n--- Customer Login ---");
        System.out.println("0. Back");
        String phone = getPhoneInput("Enter your 10-digit phone number: ");
        if(phone.equals("0")) return;

        String password = getStringInput("Enter password: ");

        if (manager.login(phone, password)) {
            Customer user = manager.getLoggedInUser();
            System.out.println("Welcome, " + user.getName() + "!");
            if (user.isLoyaltyMember()) {
                System.out.println("*** VALUED LOYALTY CUSTOMER ***");
            }
            runCustomerMenu();
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private static void runRegistration() {
        System.out.println("\n--- New Customer Registration ---");
        System.out.println("0. Back");
        String name = getStringInput("Enter your full name: ");
        if(name.equals("0")) return;

        String phone = getPhoneInput("Enter your 10-digit phone (no characters): ");
        String password = getStringInput("Enter a new password: ");

        if (manager.registerCustomer(name, phone, password)) {
            System.out.println("Registration successful! Please log in.");
        } else {
            System.out.println("Registration failed. Phone number may exist.");
        }
    }

    // --- MENUS ---

    private static void runAdminMenu() {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Book New Service (Any Customer)");
            System.out.println("2. Update/Edit Pending Service");
            System.out.println("3. Complete and Bill Service");
            System.out.println("4. Remove/Cancel a Service");
            System.out.println("5. View All Completed Logs");
            System.out.println("6. View All Pending Services");
            System.out.println("7. Logout");
            
            int choice = getIntInput("Enter choice (1-7): ", 1, 7);

            switch (choice) {
                case 1: bookNewServiceAsAdmin(); break;
                case 2: updatePendingService(); break;
                case 3: completeAndBillService(); break;
                case 4: removeService(); break;
                case 5: manager.loadLogs(); break;
                case 6: manager.listAllPendingServices(); break;
                case 7: 
                    adminRunning = false; 
                    manager.logout(); 
                    break;
            }
        }
    }

    private static void runCustomerMenu() {
        boolean customerRunning = true;
        Customer user = manager.getLoggedInUser();

        while (customerRunning) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("1. Book a New Service");
            System.out.println("2. View Status of Services");
            System.out.println("3. View My Bills");
            System.out.println("4. Logout");
            
            int choice = getIntInput("Enter choice (1-4): ", 1, 4);

            switch (choice) {
                case 1: bookNewServiceAsCustomer(user); break;
                case 2: manager.listMyStatus(user); break;
                case 3: manager.viewMyBill(user); break;
                case 4: 
                    customerRunning = false; 
                    manager.logout(); 
                    break;
            }
        }
    }

    // --- BOOKING ---

    private static void bookNewServiceAsCustomer(Customer customer) {
        System.out.println("\n--- Booking Service for " + customer.getName() + " ---");
        System.out.println("0. Cancel Booking");
        
        String make = getStringInput("Enter vehicle make: ");
        if(make.equals("0")) return;

        String model = getStringInput("Enter vehicle model: ");
        String plate = getLicensePlateInput("Enter license plate (e.g. DL5C1234): ");
        boolean isWarranty = getYesNoInput("Is vehicle under warranty?");

        List<Part> parts = new ArrayList<>();
        StringBuilder issuesList = new StringBuilder();

        int issueCount = getIntInput("How many issues are you facing?", 1, 10);

        for(int i=1; i<=issueCount; i++) {
            System.out.println("\n--- Issue " + i + " ---");
            String desc = getStringInput("Describe issue: ");
            
            System.out.println("Select part type needed:");
            System.out.println("1. Mechanical Part");
            System.out.println("2. Body Part");
            int type = getIntInput("Choice: ", 1, 2);

            if (type == 1) {
                parts.add(new MechanicalPart(desc + " Fix", 0.0));
                issuesList.append(desc).append(" (Mechanical); ");
            } else {
                parts.add(new BodyPart(desc + " Fix", 0.0));
                issuesList.append(desc).append(" (Body); ");
            }
        }
        
        String notes = getStringInput("Any notes for the mechanic? (or 'none'): ");
        
        manager.bookService(customer, new Vehicle(make, model, plate, isWarranty), issuesList.toString(), parts, notes);
    }

    private static void bookNewServiceAsAdmin() {
        System.out.println("0. Back");
        String phone = getPhoneInput("Enter customer phone: ");
        if(phone.equals("0")) return;

        Customer customer = manager.findCustomer(phone);
        
        if (customer == null) {
            System.out.println("Customer not found.");
            String name = getStringInput("Enter new customer name: ");
            customer = manager.createCustomer(name, phone);
        }
        
        bookNewServiceAsCustomer(customer); 
    }

    // --- ADMIN EDITING ---

    private static void removeService() {
        manager.listAllPendingServices();
        int index = getIntInput("Enter index to remove (0 to cancel): ", 0, 100);
        if (index == 0) return;
        manager.removeService(index);
    }

    private static void updatePendingService() {
        manager.listAllPendingServices();
        int index = getIntInput("Enter index to update (0 to cancel): ", 0, 100);
        if (index == 0) return;
        
        ServiceRecord record = manager.getPendingService(index);
        if (record == null) {
            System.out.println("Invalid index.");
            return;
        }

        boolean editing = true;
        while(editing) {
            System.out.println("\n--- Edit Service Menu ---");
            System.out.println("1. Edit Customer Issue Description");
            System.out.println("2. Add Extra Parts");
            System.out.println("3. Update Note");
            System.out.println("4. Edit Customer Details");
            System.out.println("5. Back/Exit Editing");
            
            int choice = getIntInput("Choice: ", 1, 5);

            switch (choice) {
                case 1:
                    String newIssue = getStringInput("Enter new issue description: ");
                    record.setCustomerIssue(newIssue);
                    System.out.println("Issue updated.");
                    break;
                case 2:
                    int count = getIntInput("How many extra parts?", 1, 10);
                    for(int i=0; i<count; i++) {
                        System.out.println("Part " + (i+1) + ": 1. Engine, 2. Body");
                        int pt = getIntInput("Type: ", 1, 2);
                        String pn = getStringInput("Part Name: ");
                        // --- UPDATED: Price is implicitly 0.0 for now ---
                        Part p = (pt == 1) ? new MechanicalPart(pn, 0.0) : new BodyPart(pn, 0.0);
                        record.addPart(p);
                    }
                    System.out.println("Parts added. (Pricing will be done during Billing)");
                    break;
                case 3:
                    String note = getStringInput("Enter mechanic note: ");
                    record.addMechanicNotes(note);
                    System.out.println("Note updated.");
                    break;
                case 4:
                    editCustomerDetails(record.getCustomer());
                    break;
                case 5:
                    editing = false;
                    break;
            }
        }
    }

    private static void editCustomerDetails(Customer c) {
        boolean editingCust = true;
        while(editingCust) {
            System.out.println("\n--- Edit Customer Details (" + c.getName() + ") ---");
            System.out.println("1. Update Name");
            System.out.println("2. Update Phone");
            System.out.println("3. Back");
            int ch = getIntInput("Choice: ", 1, 3);
            
            if (ch == 3) {
                editingCust = false;
                break;
            }
            
            if (ch == 1) {
                String newName = getStringInput("Enter new name: ");
                if (newName.equalsIgnoreCase(c.getName())) {
                    System.out.println("Enter different value this is same as before");
                } else {
                    c.setName(newName);
                    manager.updateCustomerData();
                    System.out.println("Name updated.");
                }
            } else if (ch == 2) {
                String newPhone = getPhoneInput("Enter new 10-digit phone: ");
                if (newPhone.equals(c.getPhone())) {
                    System.out.println("Enter different value this is same as before");
                } else {
                    c.setPhone(newPhone);
                    manager.updateCustomerData();
                    System.out.println("Phone updated.");
                }
            }
        }
    }
    
    private static void completeAndBillService() {
        manager.listAllPendingServices();
        int index = getIntInput("Enter index to bill (0 to cancel): ", 0, 100);
        if (index == 0) return;
        manager.completeAndBillService(index, scanner);
    }

    // --- INPUT HELPERS ---

    private static int getIntInput(String prompt, int min, int max) {
        int value = 0;
        while (true) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine();
                value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                if (value == 0 && min == 0) return 0;
                System.out.println("Number must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private static double getPositiveDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine();
                double val = Double.parseDouble(input);
                if (val >= 0) return val;
                System.out.println("Must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
            }
        }
    }

    private static String getStringInput(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Cannot be empty.");
        }
    }

    private static String getPhoneInput(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) return "0"; 
            if (Pattern.matches(PHONE_REGEX, input)) return input;
            System.out.println("Invalid phone. Must be exactly 10 digits (no characters).");
        }
    }

    private static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
        }
    }

    private static String getLicensePlateInput(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) return "0";
            if (Pattern.matches(LICENSE_PLATE_REGEX, input)) return input;
            System.out.println("Invalid plate. Format: DL5C1234");
        }
    }
}