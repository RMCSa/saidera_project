package com.mycompany.saidera_project.models;

public class StockItem {
    private String id;
    private String name;
    private int currentLevel;
    private int minimumLevel;
    private String unit;

    public StockItem(String id, String name, int currentLevel, int minimumLevel, String unit) {
        this.id = id;
        this.name = name;
        this.currentLevel = currentLevel;
        this.minimumLevel = minimumLevel;
        this.unit = unit;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCurrentLevel() { return currentLevel; }
    public int getMinimumLevel() { return minimumLevel; }
    public String getUnit() { return unit; }
    
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    
    public boolean isLowStock() {
        return currentLevel <= minimumLevel;
    }
}
