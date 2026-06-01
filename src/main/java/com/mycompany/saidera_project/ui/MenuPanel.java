package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.Product;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class MenuPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private java.util.List<JButton> categoryChips;
    private String selectedCategory;
    private String currentSearchTerm = "";

    public MenuPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Gestão de Cardápio");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        titlePanel.add(title);

        JLabel sub = new JLabel("Curadoria e controle de produtos do taproom.");
        sub.setFont(UIPalette.FONT_BODY);
        sub.setForeground(UIPalette.TEXT_SECONDARY);
        titlePanel.add(sub);

        header.add(titlePanel, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Novo Produto");
        addBtn.setBackground(UIPalette.AMBER);
        addBtn.setFont(UIPalette.FONT_LABEL);
        addBtn.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new ProductForm((Frame) owner, () -> refreshTable(selectedCategory)).setVisible(true);
        });
        header.add(addBtn, BorderLayout.EAST);

        JPanel northPanel = new JPanel(new BorderLayout(0, 20));
        northPanel.setOpaque(false);
        northPanel.add(header, BorderLayout.NORTH);

        // Filter Chips & Search Bar wrapper
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);

        JPanel chipsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        chipsContainer.setOpaque(false);

        categoryChips = new ArrayList<>();
        selectedCategory = null; // null = Todos
        List<String> categories = DataRepository.getInstance().getProductCategories();
        categories = new ArrayList<>(categories);
        categories.add(0, "Todos");
        
        for (String cat : categories) {
            JButton chip = new JButton(cat);
            chip.setFont(UIPalette.FONT_LABEL.deriveFont(12f));
            chip.putClientProperty("JButton.buttonType", "roundRect");
            chip.putClientProperty("categoryName", cat);
            chip.setFocusPainted(false);
            chip.addActionListener(e -> {
                selectedCategory = cat.equals("Todos") ? null : cat;
                updateChipStyles();
                refreshTable(selectedCategory);
            });
            categoryChips.add(chip);
            chipsContainer.add(chip);
        }
        filterPanel.add(chipsContainer, BorderLayout.WEST);

        // REAL-TIME SEARCH FIELD
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.putClientProperty("JTextField.placeholderText", "Buscar produto...");
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            
            private void filter() {
                currentSearchTerm = searchField.getText().trim();
                refreshTable(selectedCategory);
            }
        });
        
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField);
        filterPanel.add(searchWrapper, BorderLayout.EAST);

        northPanel.add(filterPanel, BorderLayout.CENTER);

        updateChipStyles();

        add(northPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "NOME", "CATEGORIA", "PREÇO", "STATUS", "ESTOQUE", "AÇÕES" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable
            }
        };
        refreshTable(null);

        table = new JTable(model);
        table.setRowHeight(50);

        // Setup Action Column
        TableActionCell actionCell = new TableActionCell(
                e -> JOptionPane.showMessageDialog(this, "Edição indisponível na versão demo."),
                e -> {
                    int row = Integer.parseInt(e.getActionCommand());
                    if (row < 0 || row >= model.getRowCount())
                        return;
                    table.getSelectionModel().setSelectionInterval(row, row);
                    String id = (String) model.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(this, "Excluir este produto?", "Confirmar",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DataRepository.getInstance().deleteProduct(id);
                        refreshTable(selectedCategory);
                    }
                });
        table.getColumnModel().getColumn(6).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(6).setCellEditor(actionCell);

        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);
        table.getTableHeader().setBackground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setGridColor(UIPalette.BORDER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable(String categoryFilter) {
        refreshTable(categoryFilter, currentSearchTerm);
    }

    public void refreshTable(String categoryFilter, String searchTerm) {
        model.setRowCount(0);
        List<Product> products = DataRepository.getInstance().getProducts();
        for (Product p : products) {
            boolean matchesCat = categoryFilter == null || p.getCategory().equalsIgnoreCase(categoryFilter);
            boolean matchesSearch = searchTerm == null || searchTerm.isEmpty() 
                    || p.getName().toLowerCase().contains(searchTerm.toLowerCase()) 
                    || p.getId().toLowerCase().contains(searchTerm.toLowerCase());

            if (matchesCat && matchesSearch) {
                String stockStatus = "Serviço";
                if (p.getLinkedStockItemId() != null) {
                    com.mycompany.saidera_project.models.StockItem item = DataRepository.getInstance()
                            .getStockItemById(p.getLinkedStockItemId());
                    if (item != null) {
                        stockStatus = String.format("%d %s", item.getCurrentLevel(), item.getUnit());
                    } else {
                        stockStatus = "⚠️ Erro (ID!)";
                    }
                }

                model.addRow(new Object[] {
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        String.format("R$ %.2f", p.getPrice()),
                        p.isActive() ? "ATIVO" : "INATIVO",
                        stockStatus,
                        ""
                });
            }
        }
        
        // Also update chip counts
        updateChipStyles();
    }

    private void updateChipStyles() {
        if (categoryChips == null)
            return;
        List<Product> products = DataRepository.getInstance().getProducts();
        for (JButton chip : categoryChips) {
            String cat = (String) chip.getClientProperty("categoryName");
            if (cat == null) continue;

            int count = 0;
            if (cat.equals("Todos")) {
                count = products.size();
            } else {
                for (Product p : products) {
                    if (p.getCategory().equalsIgnoreCase(cat)) {
                        count++;
                    }
                }
            }

            chip.setText(cat + " (" + count + ")");

            boolean active = (selectedCategory == null && "Todos".equals(cat))
                    || (selectedCategory != null && selectedCategory.equals(cat));
            if (active) {
                chip.setBackground(UIPalette.AMBER);
                chip.setForeground(UIPalette.ON_BACKGROUND);
            } else {
                chip.setBackground(Color.WHITE);
                chip.setForeground(UIPalette.ON_BACKGROUND);
            }
        }
    }
}
