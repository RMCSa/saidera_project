package com.mycompany.saidera_project.data;

import com.mycompany.saidera_project.models.Product;
import com.mycompany.saidera_project.models.StockItem;
import com.mycompany.saidera_project.models.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DataRepository {
    private static DataRepository instance;
    
    private List<User> users;
    private List<Product> products;
    private List<StockItem> inventory;

    private DataRepository() {
        users = new ArrayList<>();
        products = new ArrayList<>();
        inventory = new ArrayList<>();
        initializeMockData();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    private void initializeMockData() {
        // Mock Users (Password pattern: firstName123)
        users.add(new User("1", "Rafael Moreira", "rafael.moreira@choperia.com.br", "Admin", "10 Jan, 2024", "rafael123"));
        users.add(new User("2", "Vitor Henrique", "vitor.henrique@choperia.com.br", "Gerente", "15 Fev, 2024", "vitor123"));
        users.add(new User("3", "Caio Damaceno", "caio.damaceno@choperia.com.br", "Caixa", "02 Mar, 2024", "caio123"));
        users.add(new User("4", "Victor Maritan", "victor.maritan@choperia.com.br", "Garçom", "12 Mar, 2024", "victor123"));
        users.add(new User("5", "Guilherme Rodrigues", "guilherme.r@choperia.com.br", "Garçom", "20 Mar, 2024", "guilherme123"));

        // Mock Products (Linked to stock)
        products.add(new Product("01", "IPA Imperial Sunset", "Chopps", 28.00, true, "S01"));
        products.add(new Product("02", "Batata Rústica Alecrim", "Petiscos", 34.00, true, null));
        products.add(new Product("03", "Gin Tônica Verão", "Drinks", 32.00, false, "S03"));
        products.add(new Product("04", "Burger Artesanal Bacon", "Hambúrgueres", 42.00, true, null));

        // Mock Inventory
        inventory.add(new StockItem("S01", "Chopp Pilsen - Barril 50L", 2, 5, "unid."));
        inventory.add(new StockItem("S02", "Copo de Vidro 400ml", 142, 50, "unid."));
        inventory.add(new StockItem("S03", "Cilindro CO2 - 10kg", 8, 4, "unid."));
        inventory.add(new StockItem("S04", "Guardanapo de Papel Luxo", 12, 25, "pct."));
    }

    // Getters
    public List<User> getUsers() { return new ArrayList<>(users); }
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public List<StockItem> getInventory() { return new ArrayList<>(inventory); }

    public List<String> getProductCategories() {
        Set<String> categories = new LinkedHashSet<>();
        for (Product product : products) {
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                categories.add(product.getCategory().trim());
            }
        }
        return new ArrayList<>(categories);
    }

    public int getActiveProductCount() {
        int count = 0;
        for (Product product : products) {
            if (product.isActive()) count++;
        }
        return count;
    }

    public int getLinkedProductCount() {
        int count = 0;
        for (Product product : products) {
            if (product.getLinkedStockItemId() != null && !product.getLinkedStockItemId().trim().isEmpty()) count++;
        }
        return count;
    }

    public int getInactiveProductCount() {
        return getProducts().size() - getActiveProductCount();
    }

    public int getUniqueUserRoleCount() {
        Set<String> roles = new LinkedHashSet<>();
        for (User user : users) {
            if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
                roles.add(user.getRole().trim());
            }
        }
        return roles.size();
    }

    public User authenticate(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Mutation methods
    public void addProduct(Product p) { products.add(p); }
    public void deleteProduct(String id) { products.removeIf(p -> p.getId().equals(id)); }
    
    public void addUser(User u) { users.add(u); }
    public void deleteUser(String id) { users.removeIf(u -> u.getId().equals(id)); }
    
    public void addStockItem(StockItem item) { 
        inventory.add(item); 
    }

    public StockItem getStockItemById(String id) {
        if (id == null) return null;
        return inventory.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean updateStock(String itemId, int delta) {
        for (StockItem item : inventory) {
            if (item.getId().equals(itemId)) {
                int newLevel = item.getCurrentLevel() + delta;
                if (newLevel < 0) return false; // Prevent negative stock
                item.setCurrentLevel(newLevel);
                return true;
            }
        }
        return false;
    }

    public int getLowStockCount() {
        int count = 0;
        for (StockItem item : inventory) {
            if (item.isLowStock()) count++;
        }
        return count;
    }

    public double getTotalMenuPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
