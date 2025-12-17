package com.example.electricitybillcalc;


public class Bill {
    private int id;
    private int year;
    private String month;
    private double units;
    private double rebate;
    private double totalCharges;
    private double finalCost;

    // Constructors
    public Bill() {
    }

    public Bill(int year, String month, double units, double rebate, double totalCharges, double finalCost) {
        this.year = year;
        this.month = month;
        this.units = units;
        this.rebate = rebate;
        this.totalCharges = totalCharges;
        this.finalCost = finalCost;
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() { return year; }
    public String getMonth() {
        return month;
    }

    public void setYear(int year) { this.year = year; }
    public void setMonth(String month) {
        this.month = month;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getRebate() {
        return rebate;
    }

    public void setRebate(double rebate) {
        this.rebate = rebate;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(double totalCharges) {
        this.totalCharges = totalCharges;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    //tostring method
    @Override
    public String toString() {
        return month + " " + year + " - RM " + String.format("%.2f", finalCost);
    }
}