package com.service.data;

public class Customer {
    private String name;
    private String phone;
    private boolean isLoyaltyMember;

    // --- NEW FIELDS ---
    private String password;
    private boolean isAdmin;

    public Customer(String name, String phone, boolean isLoyaltyMember) {
        this.name = name;
        this.phone = phone;
        this.isLoyaltyMember = isLoyaltyMember;
        // Default password and admin status for old constructor
        this.password = "password123"; 
        this.isAdmin = false;
    }

    // --- NEW CONSTRUCTOR ---
    // We'll use this for new registrations
    public Customer(String name, String phone, String password, boolean isLoyaltyMember) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.isLoyaltyMember = isLoyaltyMember;
        this.isAdmin = false; // Default to false
    }

    // --- GETTER METHODS ---
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isLoyaltyMember() {
        return isLoyaltyMember;
    }

    // --- NEW METHODS ---
    public boolean isAdmin() {
        return isAdmin;
    }

    // A special method to create an admin
    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    // Method to check password
    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }
}