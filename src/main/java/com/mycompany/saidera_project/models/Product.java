package com.mycompany.saidera_project.models;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private boolean active;
    private String linkedStockItemId;

    public Product(String id, String name, String category, double price, boolean active, String linkedStockItemId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.active = active;
        this.linkedStockItemId = linkedStockItemId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getLinkedStockItemId() { return linkedStockItemId; }
    public void setLinkedStockItemId(String id) { this.linkedStockItemId = id; }
}
