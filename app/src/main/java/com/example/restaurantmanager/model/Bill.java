package com.example.restaurantmanager.model;

import java.util.List;

public class Bill {
    private String idTable;
    private List<FoodItemBill> foods;
    private double total;
    private String status;

    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public List<FoodItemBill> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodItemBill> foods) {
        this.foods = foods;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Getter và Setter cho các thuộc tính
}

