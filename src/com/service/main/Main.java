package com.service.main;

import com.service.core.ServiceManager;
import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.BodyPart;
import com.service.parts.EnginePart;
import com.service.parts.Part;
import com.service.records.ServiceRecord;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static ServiceManager manager = new ServiceManager();
    private static final String LICENSE_PLATE_REGEX = "^[A-Z]{2}\\d[A-Z]\\d{4}$";

    public static void main(String[] args) {
        
        boolean running = true;
        while (running) {
            // Main login/register menu
            System.out.println("\n--- Welcome to the Vehicle Service System ---");
            System.out.println("1. Login");
            System.out.println("2. Register as New Customer");
            System.out.println("3. Exit");
            
            int choice = getIntInput("Enter choice (1-3): ", 1, 3);

            switch (choice) {
                case 1:
                    runLogin();
                    break;
                case 2:
                    runRegistration();
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
            }
        }
        scanner.close();
    }

    // --- NEW METHOD: Handles the login process ---
    private static void runLogin() {
        System.out.println("\n--- Login ---");
        String phone = getStringInput("Enter phone (username): ");
        String password = getStringInput("Enter password: ");

        if (manager.login(phone, password)) {
            System.out.println("Login successful!");
            Customer user = manager.getLoggedInUser();
            
            if (user.isAdmin()) {
                System.out.println("Welcome, Admin!");
                runAdminMenu(); // Run the full admin menu
            } else {
                System.out.println("Welcome, " + user.getName() + "!");
                runCustomerMenu(); // Run the limited customer menu
            }
        } else {
            System.out.println("Login failed. Invalid phone or password.");
        }
    }

    // --- NEW METHOD: Handles new customer registration ---
    private static void runRegistration() {
        System.out.println("\n--- New Customer Registration ---");
        String name = getStringInput("Enter your full name: ");
        String phone = getStringInput("Enter your phone (this will be your username): ");
        String password = getStringInput("Enter a new password: ");

        if (manager.registerCustomer(name, phone, password)) {
            System.out.println("Registration successful! Please log in.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    // --- NEW METHOD: The Admin's Menu ---
    // This is your old main loop
    private static void runAdminMenu() {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Book New Service (for any customer)");
            System.out.println("2. Update Pending Service (Add Parts/Notes)");
            System.out.println("3. Complete and Bill Service");
            System.out.println("4. View All Completed Service Logs");
            System.out.println("5. View All Pending Services");
            System.out.println("6. Logout");
            
            int choice = getIntInput("Enter choice (1-6): ", 1, 6);

            switch (choice) {
                case 1:
                    bookNewServiceAsAdmin(); // Admin version
                    break;
                case 2:
                    updatePendingService();
                    break;
                case 3:
                    completeAndBillService();
                    break;
                case 4:
                    manager.loadLogs();
                    break;
                case 5:
                    manager.listAllPendingServices(); // Admin version
                    break;
                case 6:
                    adminRunning = false;
                    manager.logout();
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    // --- NEW METHOD: The Customer's Menu ---
    private static void runCustomerMenu() {
        boolean customerRunning = true;
        Customer user = manager.getLoggedInUser(); // Get the currently logged in customer

        while (customerRunning) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Book a New Service for My Vehicle");
            System.out.println("2. View My Pending Services");
            System.out.println("3. Logout");
            
            int choice = getIntInput("Enter choice (1-3): ", 1, 3);

            switch (choice) {
                case 1:
                    bookNewServiceAsCustomer(user); // Pass the logged-in user
                    break;
                case 2:
                    manager.listMyPendingServices(user); // Pass the logged-in user
                    break;
                case 3:
                    customerRunning = false;
                    manager.logout();
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    // --- This is the Admin booking method ---
    private static void bookNewServiceAsAdmin() {
        System.out.println("\n--- Book New Service (Admin) ---");
        String phone = getStringInput("Enter customer phone: ");
        
        Customer customer = manager.findCustomer(phone);
        if (customer == null) {
            System.out.println("New customer. Let's register them.");
            String name = getStringInput("Enter customer name: ");
            boolean isLoyalty = getYesNoInput("Is this a loyalty member?");
            // Use the old createCustomer method for admins
            customer = manager.createCustomer(name, phone, isLoyalty);
            System.out.println("Customer " + name + " registered.");
        } else {
            System.out.println("Found customer: " + customer.getName());
        }

        bookVehicleForCustomer(customer); // Call shared method
    }

    // --- This is the Customer booking method ---
    private static void bookNewServiceAsCustomer(Customer customer) {
        System.out.println("\n--- Book New Service ---");
        System.out.println("Booking for: " + customer.getName());
        bookVehicleForCustomer(customer); // Call shared method
    }

    // --- NEW SHARED METHOD ---
    // A helper method used by both admin and customer booking
    private static void bookVehicleForCustomer(Customer customer) {
        String make = getStringInput("Enter vehicle make (e.g., Toyota): ");
        String model = getStringInput("Enter vehicle model (e.g., Camry): ");
        String plate = getLicensePlateInput("Enter license plate (e.g., DL5C1234): ");
        boolean isWarranty = getYesNoInput("Is vehicle under warranty?");
        
        Vehicle vehicle = new Vehicle(make, model, plate, isWarranty);
        String issue = getStringInput("Enter customer's reported issue: ");

        manager.bookService(customer, vehicle, issue);
    }

    // This is an ADMIN-ONLY function
    private static void updatePendingService() {
        manager.listAllPendingServices(); // Show all services
        int index = getIntInput("Enter index of service to update: ", 0, 100);
        
        ServiceRecord record = manager.getPendingService(index);
        if (record == null) {
            System.out.println("Error: Invalid index.");
            return;
        }

        System.out.println("1. Add Engine Part");
        System.out.println("2. Add Body Part");
        System.out.println("3. Add Mechanic Notes");
        int choice = getIntInput("Enter choice (1-3): ", 1, 3);
        
        if (choice == 1 || choice == 2) {
            String partName = getStringInput("Enter part name: ");
            double partCost = getPositiveDoubleInput("Enter part cost: $");
            
            Part part = (choice == 1) 
                ? new EnginePart(partName, partCost)
                : new BodyPart(partName, partCost);
            record.addPart(part);
            System.out.println("Part " + partName + " added.");
        } else if (choice == 3) {
            String notes = getStringInput("Enter notes: ");
            record.addMechanicNotes(notes);
            System.out.println("Notes updated.");
        }
    }

    // This is an ADMIN-ONLY function
    private static void completeAndBillService() {
        manager.listAllPendingServices(); // Show all services
        int index = getIntInput("Enter index of service to complete and bill: ", 0, 100);
        
        ServiceRecord record = manager.getPendingService(index);
        if (record == null) {
            System.out.println("Error: Invalid index. No pending service found at " + index);
            return;
        }
        
        manager.completeAndBillService(index);
    }

    // --- All Helper methods for validation (UNCHANGED) ---

    private static int getIntInput(String prompt, int min, int max) {
        int value = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine();
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    isValid = true;
                } else {
                    System.out.println("Error: Number must be between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
            }
        }
        return value;
    }

    private static double getPositiveDoubleInput(String prompt) {
        double value = 0.0;
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine();
                value = Double.parseDouble(input);
                if (value > 0) {
                    isValid = true;
                } else {
                    System.out.println("Error: Amount must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number (e.g., 49.99).");
            }
        }
        return value;
    }

    private static String getStringInput(String prompt) {
        String input = "";
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt + " ");
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: This field cannot be empty. Please try again.");
            } else {
                isValid = true;
            }
        }
        return input;
    }

    private static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            } else {
                System.out.println("Error: Please enter only 'y' or 'n'.");
            }
        }
    }

    private static String getLicensePlateInput(String prompt) {
        String plate = "";
        boolean isValid = false;
        while (!isValid) {
            plate = getStringInput(prompt).toUpperCase();
            if (Pattern.matches(LICENSE_PLATE_REGEX, plate)) {
                isValid = true;
            } else {
                System.out.println("Error: Invalid license plate format.");
                System.out.println("Must be in the exact format: DL5C1234 (2 letters, 1 number, 1 letter, 4 numbers).");
            }
        }
        return plate;
    }
}