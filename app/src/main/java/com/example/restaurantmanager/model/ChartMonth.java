package com.example.restaurantmanager.model;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class ChartMonth {
    private List<Entry> entries;
    private String nameData;

    private String name;

    public ChartMonth(List<Entry> entries, String nameData, String name) {
        this.entries = entries;
        this.nameData = nameData;
        this.name = name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public String getNameData() {
        return nameData;
    }

    public void setNameData(String nameData) {
        this.nameData = nameData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
