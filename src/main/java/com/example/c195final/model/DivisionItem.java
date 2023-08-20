package com.example.c195final.model;

public class DivisionItem {
    private int divisionId;
    private String divisionName;

    public DivisionItem(int divisionId, String divisionName) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    @Override
    public String toString() {
        return divisionName; // This is what will be displayed in the combo box
    }
}
