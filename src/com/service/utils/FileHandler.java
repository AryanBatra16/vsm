package com.service.utils;

import com.service.records.ServiceRecord;
import com.service.parts.Part;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class FileHandler {

    private static final String LOG_FILE = "completed_service_logs.txt";

    // P2: Uses File I/O to save completed logs
    // --- UPDATED METHOD ---
    public void saveServiceLog(ServiceRecord record) {
        // P3: Exception Handling for file access errors
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            
            writer.println("======================================");
            writer.println("         COMPLETED SERVICE LOG        ");
            writer.println("======================================");
            writer.println("Customer: " + record.getCustomer().getName());
            writer.println("Vehicle: " + record.getVehicle().getMake() + " " + record.getVehicle().getModel());
            writer.println("Technician: " + record.getTechnician().getName());
            writer.println("---");
            writer.println("Issue Reported: " + record.getCustomerIssue());
            writer.println("Mechanic Notes: " + record.getMechanicNotes());
            writer.println("---");
            writer.println("Parts Used:");
            
            Part[] parts = record.getPartsUsed();
            for (int i = 0; i < record.getPartCount(); i++) {
                writer.println("  - " + parts[i].getPartName() + ": $" + parts[i].getPartCost());
            }

            // --- UPDATED OUTPUT BLOCK ---
            writer.println("---");
            writer.println("Billing Details:");
            writer.println("  Parts:    $" + record.getPartsCost());
            writer.println("  Labor:    $" + record.getLaborCost());
            writer.println("  Subtotal: $" + record.getSubtotal());
            writer.println("  Discount: -$" + record.getDiscount());
            writer.println("  --------------------");
            writer.println("  TOTAL BILL: $" + record.getTotalServiceCost());
            writer.println("\n");
            // --- END OF BLOCK ---

            System.out.println("Service log saved to " + LOG_FILE);

        } catch (IOException e) {
            // P3: Catching file access errors
            System.err.println("Error: Could not write to log file.");
            e.printStackTrace();
        }
    }

    // P2: Uses File I/O to load logs
    public void loadAllServiceLogs() {
        System.out.println("\n--- Loading All Completed Service Logs ---");
        // P3: Exception Handling for file access errors
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No completed service logs found.");
        } catch (IOException e) {
            System.err.println("Error: Could not read log file.");
            e.printStackTrace();
        }
        System.out.println("--- End of Logs ---");
    }
}