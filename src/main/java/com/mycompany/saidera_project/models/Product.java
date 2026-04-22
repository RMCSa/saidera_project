package com.mycompany.saidera_project.models;

/**
 * Entidade que representa um produto à venda no sistema (Menu).
 * Armazena informações como preço, categoria e status, podendo
 * ser atrelado a um item do estoque para controle de disponibilidade.
 */
public class Product {
    /** Identificador único do produto. */
    private String id;
    /** Nome comercial do produto. */
    private String name;
    /** Categoria à qual o produto pertence (ex: Chopps, Petiscos). */
    private String category;
    /** Preço de venda do produto. */
    private double price;
    /** Indica se o produto está ativo e visível no menu. */
    private boolean active;
    /** ID do item de estoque atrelado (opcional), referenciando um StockItem. */
    private String linkedStockItemId;

    /**
     * Construtor para criar um novo Produto.
     * 
     * @param id Identificador único
     * @param name Nome do produto
     * @param category Categoria do produto
     * @param price Preço do produto
     * @param active Estado de ativação
     * @param linkedStockItemId Identificador do item de estoque relacionado
     */
    public Product(String id, String name, String category, double price, boolean active, String linkedStockItemId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.active = active;
        this.linkedStockItemId = linkedStockItemId;
    }

    // Getters and Setters
    /** @return O identificador do produto */
    public String getId() { return id; }
    /** @return O nome do produto */
    public String getName() { return name; }
    /** @return A categoria do produto */
    public String getCategory() { return category; }
    /** @return O preço do produto */
    public double getPrice() { return price; }
    /** @return True se o produto estiver ativo, false caso contrário */
    public boolean isActive() { return active; }
    /** @param active Define o estado de atividade do produto */
    public void setActive(boolean active) { this.active = active; }
    /** @return O ID do estoque atrelado ou null se não houver */
    public String getLinkedStockItemId() { return linkedStockItemId; }
    /** @param id Altera o ID do item de estoque ligado ao produto */
    public void setLinkedStockItemId(String id) { this.linkedStockItemId = id; }
}
