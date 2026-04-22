package com.mycompany.saidera_project.models;

/**
 * Entidade que representa um suprimento ou matéria-prima no estoque.
 * Mantém o controle sobre os níveis atuais e mínimos para gerar alertas.
 */
public class StockItem {
    /** Identificador único do item no estoque. */
    private String id;
    /** Nome do item no estoque. */
    private String name;
    /** Quantidade atual disponível no estoque. */
    private int currentLevel;
    /** Quantidade mínima segura; abaixo deste valor dispara alerta de estoque baixo. */
    private int minimumLevel;
    /** Unidade de medida (ex: kg, L, un, pct). */
    private String unit;

    /**
     * Construtor para inicializar um item de estoque.
     * 
     * @param id O identificador único
     * @param name Nome do item
     * @param currentLevel Nível atual em estoque
     * @param minimumLevel Nível de corte para alerta
     * @param unit Unidade de medida do item
     */
    public StockItem(String id, String name, int currentLevel, int minimumLevel, String unit) {
        this.id = id;
        this.name = name;
        this.currentLevel = currentLevel;
        this.minimumLevel = minimumLevel;
        this.unit = unit;
    }

    // Getters and Setters
    /** @return O identificador do item */
    public String getId() { return id; }
    /** @return O nome do item */
    public String getName() { return name; }
    /** @return A quantidade atual disponível */
    public int getCurrentLevel() { return currentLevel; }
    /** @return O nível de estoque de segurança/mínimo */
    public int getMinimumLevel() { return minimumLevel; }
    /** @return A unidade de medida atribuída */
    public String getUnit() { return unit; }
    
    /** @param currentLevel Define uma nova quantidade no nível atual */
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    
    /**
     * Verifica se o item atingiu o nível crítico.
     * 
     * @return True se a quantidade atual for menor ou igual à mínima.
     */
    public boolean isLowStock() {
        return currentLevel <= minimumLevel;
    }
}
