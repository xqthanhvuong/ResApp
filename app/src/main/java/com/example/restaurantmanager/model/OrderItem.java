package com.example.restaurantmanager.model;


public class OrderItem {
    private final String id;
    private final String name;
    private final float price;
    private final String imageUrl;

    public OrderItem(String id, String name, float price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }
}