package com.mycompany.saidera_project.data;

import com.mycompany.saidera_project.models.Product;
import com.mycompany.saidera_project.models.StockItem;
import com.mycompany.saidera_project.models.User;
import java.util.ArrayList;
import java.util.List;

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
        // Mock Users
        users.add(new User("AM", "André Machado", "andre.machado@choperia.com.br", "Admin", "12 Jan, 2023"));
        users.add(new User("BS", "Beatriz Silva", "beatriz.silva@choperia.com.br", "Caixa", "05 Mar, 2023"));
        users.add(new User("CR", "Carlos Roberto", "carlos.r@choperia.com.br", "Garçom", "18 Out, 2023"));
        users.add(new User("FP", "Fernanda Pires", "fernanda.pires@choperia.com.br", "Garçom", "02 Jan, 2024"));

        // Mock Products
        products.add(new Product("01", "IPA Imperial Sunset", "Chopps", 28.00, true));
        products.add(new Product("02", "Batata Rústica Alecrim", "Petiscos", 34.00, true));
        products.add(new Product("03", "Gin Tônica Verão", "Drinks", 32.00, false));
        products.add(new Product("04", "Burger Artesanal Bacon", "Hambúrgueres", 42.00, true));

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

    // Mutation methods
    public void addProduct(Product p) { products.add(p); }
    public void deleteProduct(String id) { products.removeIf(p -> p.getId().equals(id)); }
    
    public void addUser(User u) { users.add(u); }
    public void deleteUser(String id) { users.removeIf(u -> u.getId().equals(id)); }

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
