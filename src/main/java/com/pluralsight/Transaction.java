package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount){
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getDate(){
        return date;
    }

    public LocalTime getTime(){
        return time;
    }

    public String getDescription(){
        return description;
    }

    public String getVendor(){
        return vendor;
    }

    public double getAmount(){
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction: " + getDate() + getTime() + getDescription()  + getVendor()  + getAmount();
    }
}