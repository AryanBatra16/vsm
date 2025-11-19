package com.service.utils;

import com.service.records.ServiceRecord;
import com.service.data.Customer;
import com.service.parts.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String LOG_FILE = "completed_service_logs.txt";
    private static final String USERS_FILE = "users.txt";

    public void saveUsers(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (Customer c : customers) {
                writer.println(c.toString()); 
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save user data.");
        }
    }

    public List<Customer> loadUsers() {
        List<Customer> loadedCustomers = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return loadedCustomers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    loadedCustomers.add(new Customer(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3]), Integer.parseInt(parts[4])));
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load user data.");
        }
        return loadedCustomers;
    }

    public void saveServiceLog(ServiceRecord record) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("======================================");
            writer.println("         COMPLETED SERVICE LOG        ");
            writer.println("======================================");
            writer.println("Customer: " + record.getCustomer().getName());
            writer.println("Vehicle: " + record.getVehicle().getMake() + " " + record.getVehicle().getModel());
            writer.println("---");
            writer.println("Parts Breakdown:");
            for (Part p : record.getPartsUsed()) {
                writer.println(String.format(" - %-20s : $%.2f (Labor: $%.2f)", p.getPartName(), p.getPartCost(), p.calculateLaborCost()));
            }
            writer.println("---");
            writer.println(String.format("Parts Total:   $%.2f", record.getPartsCost()));
            writer.println(String.format("Labor Total:   $%.2f", record.getLaborCost()));
            writer.println(String.format("Subtotal:      $%.2f", record.getSubtotal()));
            writer.println(String.format("Discount:     -$%.2f", record.getDiscount()));
            writer.println("--------------------------------------");
            writer.println(String.format("TOTAL BILL (Tax Inclusive): $%.2f", record.getTotalServiceCost()));
            writer.println("\n");
        } catch (IOException e) {
            System.err.println("Error: Could not write to log file.");
        }
    }

    public void loadAllServiceLogs() {
        System.out.println("\n--- Loading All Completed Service Logs ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No completed service logs found.");
        } catch (IOException e) {
            System.err.println("Error reading log file.");
        }
        System.out.println("--- End of Logs ---");
    }
}