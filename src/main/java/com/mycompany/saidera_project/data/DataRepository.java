package com.mycompany.saidera_project.data;

import com.mycompany.saidera_project.models.Product;
import com.mycompany.saidera_project.models.StockItem;
import com.mycompany.saidera_project.models.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Repositório de dados em memória utilizando o padrão Singleton.
 * Fornece métodos CRUD para manipular informações de estoque, produtos e usuários.
 */
public class DataRepository {
    /** Instância ativa e única da classe. */
    private static DataRepository instance;
    
    /** Lista de usuários contidos no sistema. */
    private List<User> users;
    /** Lista de produtos disponíveis no cardápio. */
    private List<Product> products;
    /** Lista de itens supridos e controlados pelo estoque. */
    private List<StockItem> inventory;

    /**
     * Construtor privado: inicializa as listas e gera dados fictícios (Mock).
     */
    private DataRepository() {
        users = new ArrayList<>();
        products = new ArrayList<>();
        inventory = new ArrayList<>();
        initializeMockData();
    }

    /**
     * Ponto de acesso à instância única da classe (Singleton).
     *
     * @return A instância global de DataRepository.
     */
    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    /**
     * Inicializa os dados fictícios mockados da inicialização da aplicação
     * para demonstração do sistema.
     */
    private void initializeMockData() {
        // Mock Users (Password pattern: firstName123)
        users.add(new User("1", "Rafael Moreira", "rafael.moreira@choperia.com.br", "Admin", "10 Jan, 2024", "rafael123"));
        users.add(new User("2", "Vitor Henrique", "vitor.henrique@choperia.com.br", "Gerente", "15 Fev, 2024", "vitor123"));
        users.add(new User("3", "Caio Damaceno", "caio.damaceno@choperia.com.br", "Caixa", "02 Mar, 2024", "caio123"));
        users.add(new User("4", "Victor Maritan", "victor.maritan@choperia.com.br", "Garçom", "12 Mar, 2024", "victor123"));
        users.add(new User("5", "Guilherme Rodrigues", "guilherme.r@choperia.com.br", "Garçom", "20 Mar, 2024", "guilherme123"));
        users.add(new User("6", "Admin", "admin", "Admin", "20 Mar, 2024", "password"));

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

    /** @return Uma cópia da lista atual de usuários registrados. */
    public List<User> getUsers() { return new ArrayList<>(users); }
    /** @return Uma cópia da lista atual de produtos cadastrados. */
    public List<Product> getProducts() { return new ArrayList<>(products); }
    /** @return Uma cópia da lista atual de itens de estoque. */
    public List<StockItem> getInventory() { return new ArrayList<>(inventory); }

    /**
     * Recupera e deduz categorias únicas de produtos com base nos itens cadastrados.
     *
     * @return Uma lista limpa de nomes de categorias ativas.
     */
    public List<String> getProductCategories() {
        Set<String> categories = new LinkedHashSet<>();
        for (Product product : products) {
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                categories.add(product.getCategory().trim());
            }
        }
        return new ArrayList<>(categories);
    }

    /**
     * Calcula o número de produtos ativos no menu.
     * 
     * @return A quantidade de produtos cuja propriedade active é verdadeira.
     */
    public int getActiveProductCount() {
        int count = 0;
        for (Product product : products) {
            if (product.isActive()) count++;
        }
        return count;
    }

    /**
     * Calcula o número de produtos que possuem um item de estoque atrelado.
     * 
     * @return O número de produtos vinculados.
     */
    public int getLinkedProductCount() {
        int count = 0;
        for (Product product : products) {
            if (product.getLinkedStockItemId() != null && !product.getLinkedStockItemId().trim().isEmpty()) count++;
        }
        return count;
    }

    /**
     * Calcula a quantidade de produtos desativados atualmente.
     * 
     * @return Número total de inativos.
     */
    public int getInactiveProductCount() {
        return getProducts().size() - getActiveProductCount();
    }

    /**
     * Retorna a contagem de papéis (cargos) únicos atualmente cadastrados na lista de usuários.
     * 
     * @return A quantidade de papéis (roles) distintos.
     */
    public int getUniqueUserRoleCount() {
        Set<String> roles = new LinkedHashSet<>();
        for (User user : users) {
            if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
                roles.add(user.getRole().trim());
            }
        }
        return roles.size();
    }

    /**
     * Valida as credenciais de um usuário em busca de autenticação funcional.
     * 
     * @param email O e-mail informado no login.
     * @param password A senha informada no login.
     * @return O objeto User correspondente se for validado, ou null em falha.
     */
    public User authenticate(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Mutation methods
    
    /** @param p Adiciona um novo produto ao sistema. */
    public void addProduct(Product p) { products.add(p); }
    
    /** @param id Deleta o produto correspondente ao ID informado. */
    public void deleteProduct(String id) { products.removeIf(p -> p.getId().equals(id)); }
    
    /** @param u Registra um novo usuário no sistema. */
    public void addUser(User u) { users.add(u); }
    
    /** @param id Deleta o usuário baseado em seu ID. */
    public void deleteUser(String id) { users.removeIf(u -> u.getId().equals(id)); }
    
    /** @param item Registra um novo item de estoque no armazenamento global. */
    public void addStockItem(StockItem item) { 
        inventory.add(item); 
    }

    /**
     * Busca e retorna um item específico cadastrado no estoque de acordo com seu identificador.
     * 
     * @param id ID do estoque a localizar.
     * @return Objeto StockItem mapeado ou null se inexistente.
     */
    public StockItem getStockItemById(String id) {
        if (id == null) return null;
        return inventory.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Altera a quantidade existente de um produto estocado por meio de uma variação.
     * 
     * @param itemId ID do item no estoque.
     * @param delta Quantidade à subtrair (com um sinal negativo) ou somar (sinal positivo).
     * @return True se a transação for aceita, sem resultar em estoque negativo, false em fracasso.
     */
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

    /**
     * Retorna a volumetria exata de itens que bateram o limite do 'low stock' e 
     * precisam de atenção de recompra.
     * 
     * @return Número de componentes em estoque baixo.
     */
    public int getLowStockCount() {
        int count = 0;
        for (StockItem item : inventory) {
            if (item.isLowStock()) count++;
        }
        return count;
    }

    /**
     * Totaliza o valor do menu varrendo os preços somados de todos os produtos atuantes.
     * 
     * @return A soma escalar monetária global do menu.
     */
    public double getTotalMenuPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
