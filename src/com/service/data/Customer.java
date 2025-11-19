package com.service.data;

import java.io.Serializable;

public class Customer implements Serializable {
    private String name;
    private String phone;
    private String password;
    private boolean isAdmin;
    private int serviceCount; 

    public Customer(String name, String phone, String password, boolean isAdmin, int serviceCount) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
        this.serviceCount = serviceCount;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }
    public int getServiceCount() { return serviceCount; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isLoyaltyMember() {
        return serviceCount > 1; 
    }

    public void incrementServiceCount() {
        this.serviceCount++;
    }

    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

    @Override
    public String toString() {
        return name + "," + phone + "," + password + "," + isAdmin + "," + serviceCount;
    }
}