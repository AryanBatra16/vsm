package com.service.utils;

import com.service.records.ServiceRecord;
import com.service.data.Customer;
import com.service.data.Vehicle;
import com.service.parts.Part;
import com.service.parts.MechanicalPart;
import com.service.parts.BodyPart;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String LOG_FILE = "service_data.csv";
    private static final String USERS_FILE = "users.csv";
    private static final String PENDING_SERVICES_FILE = "pending_services.csv";

    public void saveUsers(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("Name,Phone,Password,IsAdmin,ServiceCount");
            
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

    public void saveServiceLog(ServiceRecord record) {
        boolean fileExists = new File(LOG_FILE).exists();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            if (!fileExists) {
                writer.println("Customer Name,Vehicle Plate,Issue Description,Parts Cost,Labor Cost,Discount,Total Bill");
            }

            String customer = record.getCustomer().getName().replace(",", " ");
            String plate = record.getVehicle().getLicensePlate();
            String issue = record.getCustomerIssue().replace(",", ";");
            
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
            reader.readLine(); 

            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    count++;
                    System.out.println("Log #" + count);
                    System.out.println(" Customer: " + data[0]);
                    System.out.println(" Vehicle:  " + data[1]);
                    System.out.println(" Issue:    " + data[2]);
                    System.out.println(" Total:    Rs. " + data[6]);
                    System.out.println("---------------------------");
                }
            }
            if (count == 0) System.out.println("File exists but is empty.");
            
        } catch (IOException e) {
            System.err.println("Error: Could not read log file.");
        }
        System.out.println("--- End of Logs ---");
    }

    public void savePendingServices(List<ServiceRecord> pendingServices) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PENDING_SERVICES_FILE))) {
            writer.println("CustomerName,CustomerPhone,VehicleMake,VehicleModel,VehiclePlate,Warranty,Issue,TechnicianName,Notes,PartsInfo");
            
            for (ServiceRecord record : pendingServices) {
                String customerName = record.getCustomer().getName().replace(",", " ");
                String customerPhone = record.getCustomer().getPhone();
                String vehicleMake = record.getVehicle().getMake().replace(",", " ");
                String vehicleModel = record.getVehicle().getModel().replace(",", " ");
                String vehiclePlate = record.getVehicle().getLicensePlate();
                boolean warranty = record.getVehicle().isUnderWarranty();
                String issue = record.getCustomerIssue().replace(",", ";");
                String techName = record.getTechnician() != null ? record.getTechnician().getName() : "None";
                String notes = record.getMechanicNotes().replace(",", ";");
                
                StringBuilder partsInfo = new StringBuilder();
                for (Part part : record.getPartsUsed()) {
                    String partType = (part instanceof MechanicalPart) ? "M" : "B";
                    String partName = part.getPartName().replace(",", " ").replace("|", " ");
                    double partCost = part.getPartCost();
                    partsInfo.append(partType).append("|").append(partName).append("|").append(partCost).append(";");
                }
                String partsStr = partsInfo.length() > 0 ? partsInfo.toString() : "NONE";
                
                writer.printf("%s,%s,%s,%s,%s,%b,%s,%s,%s,%s%n",
                    customerName, customerPhone, vehicleMake, vehicleModel, vehiclePlate,
                    warranty, issue, techName, notes, partsStr);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save pending services.");
        }
    }

    public List<ServiceRecord> loadPendingServices() {
        List<ServiceRecord> loadedServices = new ArrayList<>();
        File file = new File(PENDING_SERVICES_FILE);
        if (!file.exists()) return loadedServices;

        List<Customer> customers = loadUsers();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 10) {
                    String customerPhone = parts[1];
                    Customer customer = findCustomerByPhone(customers, customerPhone);
                    if (customer == null) continue;

                    String vehicleMake = parts[2];
                    String vehicleModel = parts[3];
                    String vehiclePlate = parts[4];
                    boolean warranty = Boolean.parseBoolean(parts[5]);
                    String issue = parts[6];
                    String notes = parts[8];
                    String partsStr = parts[9];

                    Vehicle vehicle = new Vehicle(vehicleMake, vehicleModel, vehiclePlate, warranty);
                    ServiceRecord record = new ServiceRecord(customer, vehicle, issue);
                    
                    if (!notes.equals("No notes yet.")) {
                        record.addMechanicNotes(notes);
                    }

                    if (!partsStr.equals("NONE")) {
                        String[] partsList = partsStr.split(";");
                        for (String partInfo : partsList) {
                            if (partInfo.trim().isEmpty()) continue;
                            String[] partData = partInfo.split("\\|");
                            if (partData.length == 3) {
                                String partType = partData[0];
                                String partName = partData[1];
                                double partCost = Double.parseDouble(partData[2]);
                                
                                Part part = partType.equals("M") 
                                    ? new MechanicalPart(partName, partCost)
                                    : new BodyPart(partName, partCost);
                                record.addPart(part);
                            }
                        }
                    }

                    loadedServices.add(record);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load pending services.");
        }
        return loadedServices;
    }

    private Customer findCustomerByPhone(List<Customer> customers, String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone)) {
                return c;
            }
        }
        return null;
    }
}