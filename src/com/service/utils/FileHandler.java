package com.service.utils;

import com.service.records.ServiceRecord;
import com.service.data.Customer;
import com.service.parts.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Changed extensions to .csv so they open in Excel
    private static final String LOG_FILE = "service_data.csv";
    private static final String USERS_FILE = "users.csv";

    // --- USER PERSISTENCE (Excel Compatible) ---

    public void saveUsers(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            // Add an Excel Header Row
            writer.println("Name,Phone,Password,IsAdmin,ServiceCount");
            
            for (Customer c : customers) {
                // Customer.toString() is already formatted as: name,phone,pass...
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
            // Skip the first line (Header row)
            reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String name = parts[0];
                    String phone = parts[1];
                    String pass = parts[2];
                    boolean isAdmin = Boolean.parseBoolean(parts[3]);
                    int count = Integer.parseInt(parts[4]);
                    loadedCustomers.add(new Customer(name, phone, pass, isAdmin, count));
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load user data.");
        }
        return loadedCustomers;
    }

    // --- SERVICE LOGS (Excel Compatible) ---

    public void saveServiceLog(ServiceRecord record) {
        boolean fileExists = new File(LOG_FILE).exists();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            // If creating a new file, add the Excel Header Row first
            if (!fileExists) {
                writer.println("Customer Name,Vehicle Plate,Issue Description,Parts Cost,Labor Cost,Discount,Total Bill");
            }

            // Prepare data for CSV format (avoiding commas in text to keep columns safe)
            String customer = record.getCustomer().getName().replace(",", " ");
            String plate = record.getVehicle().getLicensePlate();
            String issue = record.getCustomerIssue().replace(",", ";"); // Replace commas in issue to avoid breaking CSV
            
            // Write a single row for this service
            writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%.2f%n", 
                customer, 
                plate, 
                issue, 
                record.getPartsCost(), 
                record.getLaborCost(), 
                record.getDiscount(), 
                record.getTotalServiceCost()
            );
            
            System.out.println("Log saved to " + LOG_FILE + " (Opens in Excel)");

        } catch (IOException e) {
            System.err.println("Error: Could not write to log file.");
        }
    }

    public void loadAllServiceLogs() {
        System.out.println("\n--- Loading All Completed Service Logs ---");
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Read and print header
            String header = reader.readLine(); 
            // System.out.println("[Header]: " + header); 

            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    count++;
                    // Format the CSV data back into a readable block for the console
                    System.out.println("Log #" + count);
                    System.out.println(" Customer: " + data[0]);
                    System.out.println(" Vehicle:  " + data[1]);
                    System.out.println(" Issue:    " + data[2]);
                    System.out.println(" Total:    $" + data[6]);
                    System.out.println("---------------------------");
                }
            }
            if (count == 0) System.out.println("File exists but is empty.");
            
        } catch (IOException e) {
            System.err.println("Error: Could not read log file.");
        }
        System.out.println("--- End of Logs ---");
    }
}