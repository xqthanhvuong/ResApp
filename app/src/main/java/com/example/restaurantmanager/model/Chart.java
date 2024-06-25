package com.example.restaurantmanager.model;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import java.util.List;

public class Chart {
    private List<Entry> entries1;
    private String name1;
    private List<Entry> entries2;

    private String name2;

    private String name;

    public Chart(String name, List<Entry> entries1, List<Entry> entries2, String name1, String name2) {
        this.entries1 = entries1;
        this.entries2 = entries2;
        this.name=name;
        this.name1=name1;
        this.name2=name2;
    }

    public List<Entry> getEntries2() {
        return entries2;
    }

    public void setEntries2(List<Entry> entries2) {
        this.entries2 = entries2;
    }

    public List<Entry> getEntries1() {
        return entries1;
    }

    public void setEntries1(List<Entry> entries1) {
        this.entries1 = entries1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }
}
