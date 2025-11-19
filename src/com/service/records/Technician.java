package com.service.records;

import java.io.Serializable;

public class Technician implements Serializable {
    private String name;
    private String specialization;

    public Technician(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
}