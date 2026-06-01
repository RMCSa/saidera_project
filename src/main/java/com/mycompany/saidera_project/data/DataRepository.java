package com.mycompany.saidera_project.data;

import com.mycompany.saidera_project.models.Product;
import com.mycompany.saidera_project.models.StockItem;
import com.mycompany.saidera_project.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DataRepository {
    private static DataRepository instance;

    private DataRepository() {
        initializeDatabaseIfEmpty();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    private void initializeDatabaseIfEmpty() {
        // Auto-fix table column length for username if needed
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("ALTER TABLE \"User\" ALTER COLUMN username TYPE VARCHAR(100)")) {
            stmt.executeUpdate();
            System.out.println("Tabela 'User' alterada com sucesso para suportar e-mails de ate 100 caracteres.");
        } catch (SQLException e) {
            // Quietly print warning if we can't alter column type (e.g. if db is not ready, or table is missing)
            System.err.println("Aviso ao ajustar tamanho da coluna username: " + e.getMessage());
        }

        String countSql = "SELECT COUNT(*) FROM \"User\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Banco de dados vazio. Semeando usuario administrador padrao...");
                User defaultAdmin = new User("admin-id", "Rafael Moreira", "rafael.moreira@choperia.com.br", "Admin", "10 Jan, 2024", "rafael123");
                addUser(defaultAdmin);
                
                // Semear alguns itens de estoque / produtos caso a tabela de produtos tambem esteja vazia
                seedDefaultProductsIfEmpty();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar/semear banco de dados: " + e.getMessage());
        }
    }

    private void seedDefaultProductsIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM \"Product\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Tabela de produtos vazia. Semeando itens de teste...");
                // Add some default products mapped to Prisma Schema columns
                // IPA Imperial Sunset
                addProductDirect("S01", "IPA Imperial Sunset", "Chopps", 28.00, 2.0, 5.0, "L");
                // Batata Rustica
                addProductDirect("S02", "Batata Rustica Alecrim", "Petiscos", 34.00, 0.0, 0.0, "UN");
                // Gin Tonica
                addProductDirect("S03", "Gin Tonica Verao", "Drinks", 32.00, 8.0, 4.0, "UN");
                // Burger Artesanal
                addProductDirect("S04", "Burger Artesanal Bacon", "Hambúrgueres", 42.00, 0.0, 0.0, "UN");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao semear produtos padrao: " + e.getMessage());
        }
    }

    private void addProductDirect(String id, String name, String category, double price, double stock, double minStock, String unit) {
        String sql = "INSERT INTO \"Product\" (id, name, description, \"sellingPrice\", \"unitOfMeasure\", category, stock, \"minStockLevel\", \"createdAt\", \"updatedAt\") VALUES (?, ?, ?, ?, ?, ?::\"ProductCategory\", ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, "Item inicial semeado");
            stmt.setDouble(4, price);
            stmt.setString(5, unit);
            stmt.setString(6, mapCategoryJavaToDb(category));
            stmt.setDouble(7, stock);
            stmt.setDouble(8, minStock);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(9, now);
            stmt.setTimestamp(10, now);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir produto " + name + ": " + e.getMessage());
        }
    }

    // Getters
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM \"User\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userList.add(new User(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("username"), // mapped to email in Java
                    mapRoleDbToJava(rs.getString("role")),
                    formatDate(rs.getTimestamp("createdAt")),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM \"Product\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                productList.add(new Product(
                    id,
                    rs.getString("name"),
                    mapCategoryDbToJava(rs.getString("category")),
                    rs.getDouble("sellingPrice"),
                    true, // active is always true since column doesn't exist
                    id    // self-linked stock ID
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public List<StockItem> getInventory() {
        List<StockItem> stockList = new ArrayList<>();
        String sql = "SELECT * FROM \"Product\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stockList.add(new StockItem(
                    rs.getString("id"),
                    rs.getString("name"),
                    (int) rs.getDouble("stock"),
                    (int) rs.getDouble("minStockLevel"),
                    rs.getString("unitOfMeasure")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockList;
    }

    public List<String> getProductCategories() {
        Set<String> categories = new LinkedHashSet<>();
        for (Product product : getProducts()) {
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                categories.add(product.getCategory().trim());
            }
        }
        return new ArrayList<>(categories);
    }

    public int getActiveProductCount() {
        return getProducts().size();
    }

    public int getLinkedProductCount() {
        return getProducts().size();
    }

    public int getInactiveProductCount() {
        return 0;
    }

    public int getUniqueUserRoleCount() {
        Set<String> roles = new java.util.HashSet<>();
        for (User user : getUsers()) {
            roles.add(user.getRole());
        }
        return roles.size();
    }

    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM \"User\" WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("username"), // mapped to email
                        mapRoleDbToJava(rs.getString("role")),
                        formatDate(rs.getTimestamp("createdAt")),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Mutation methods
    public void addProduct(Product p) {
        String sql = "INSERT INTO \"Product\" (id, name, description, \"sellingPrice\", \"unitOfMeasure\", category, stock, \"minStockLevel\", \"createdAt\", \"updatedAt\") VALUES (?, ?, ?, ?, ?, ?::\"ProductCategory\", ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getId());
            stmt.setString(2, p.getName());
            stmt.setString(3, "Item criado via app desktop");
            stmt.setDouble(4, p.getPrice());
            stmt.setString(5, "UN");
            stmt.setString(6, mapCategoryJavaToDb(p.getCategory()));
            stmt.setDouble(7, 0.0);
            stmt.setDouble(8, 0.0);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(9, now);
            stmt.setTimestamp(10, now);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(String id) {
        String sql = "DELETE FROM \"Product\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User u) {
        String sql = "INSERT INTO \"User\" (id, name, username, password, role, \"createdAt\", \"updatedAt\") VALUES (?, ?, ?, ?, ?::\"UserRole\", ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getId());
            stmt.setString(2, u.getName());
            stmt.setString(3, u.getEmail()); // mapped to username
            stmt.setString(4, u.getPassword());
            stmt.setString(5, mapRoleJavaToDb(u.getRole()));
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(6, now);
            stmt.setTimestamp(7, now);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String id) {
        String sql = "DELETE FROM \"User\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStockItem(StockItem item) {
        // Since there is no separate stock_items table, we save it as a product with category OTHER and sellingPrice = 0
        String sql = "INSERT INTO \"Product\" (id, name, description, \"sellingPrice\", \"unitOfMeasure\", category, stock, \"minStockLevel\", \"createdAt\", \"updatedAt\") VALUES (?, ?, ?, ?, ?, ?::\"ProductCategory\", ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, "Insumo de estoque cadastrado");
            stmt.setDouble(4, 0.0);
            stmt.setString(5, item.getUnit());
            stmt.setString(6, "OTHER");
            stmt.setDouble(7, item.getCurrentLevel());
            stmt.setDouble(8, item.getMinimumLevel());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(9, now);
            stmt.setTimestamp(10, now);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public StockItem getStockItemById(String id) {
        if (id == null) return null;
        String sql = "SELECT * FROM \"Product\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StockItem(
                        rs.getString("id"),
                        rs.getString("name"),
                        (int) rs.getDouble("stock"),
                        (int) rs.getDouble("minStockLevel"),
                        rs.getString("unitOfMeasure")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStock(String itemId, int delta) {
        String selectSql = "SELECT stock FROM \"Product\" WHERE id = ?";
        String updateSql = "UPDATE \"Product\" SET stock = ?, \"updatedAt\" = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double currentStock = 0;
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                    selectStmt.setString(1, itemId);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            currentStock = rs.getDouble("stock");
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                double newStock = currentStock + delta;
                if (newStock < 0) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, newStock);
                    updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    updateStmt.setString(3, itemId);
                    updateStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLowStockCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM \"Product\" WHERE stock <= \"minStockLevel\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public double getTotalMenuPrice() {
        double total = 0;
        String sql = "SELECT SUM(\"sellingPrice\") FROM \"Product\"";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Helper mappings for Roles and Categories
    private String mapRoleJavaToDb(String javaRole) {
        if (javaRole == null) return "WAITER";
        switch (javaRole) {
            case "Admin":
            case "Gerente":
                return "ADMIN";
            case "Caixa":
                return "CASHIER";
            case "Garçom":
            default:
                return "WAITER";
        }
    }

    private String mapRoleDbToJava(String dbRole) {
        if (dbRole == null) return "Garçom";
        switch (dbRole) {
            case "ADMIN":
                return "Admin";
            case "CASHIER":
                return "Caixa";
            case "WAITER":
            default:
                return "Garçom";
        }
    }

    private String mapCategoryJavaToDb(String javaCategory) {
        if (javaCategory == null) return "OTHER";
        String lower = javaCategory.toLowerCase();
        if (lower.contains("chopp") || lower.contains("cerveja") || lower.contains("ipa")) {
            return "CHOPP";
        } else if (lower.contains("drink") || lower.contains("cocktail") || lower.contains("bebida")) {
            return "DRINK";
        } else if (lower.contains("petisco") || lower.contains("hamb") || lower.contains("burg") || lower.contains("comida") || lower.contains("food")) {
            return "FOOD";
        } else {
            return "OTHER";
        }
    }

    private String mapCategoryDbToJava(String dbCategory) {
        if (dbCategory == null) return "Outros";
        switch (dbCategory) {
            case "CHOPP":
                return "Chopps";
            case "FOOD":
                return "Petiscos";
            case "DRINK":
                return "Drinks";
            case "OTHER":
            default:
                return "Outros";
        }
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        return new SimpleDateFormat("dd MMM, yyyy", java.util.Locale.forLanguageTag("pt-BR")).format(timestamp);
    }
}
