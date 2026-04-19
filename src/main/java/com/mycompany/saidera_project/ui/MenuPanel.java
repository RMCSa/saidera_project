package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.Product;
import java.util.List;
import java.awt.*;

public class MenuPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    public MenuPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(40, 40, 40, 40));

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
            new ProductForm((Frame) owner, () -> refreshTable(null)).setVisible(true);
        });
        header.add(addBtn, BorderLayout.EAST);
        
        JPanel northPanel = new JPanel(new BorderLayout(0, 20));
        northPanel.setOpaque(false);
        northPanel.add(header, BorderLayout.NORTH);

        // Filter Chips
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        String[] categories = {"Todos", "Chopps", "Petiscos", "Drinks", "Hambúrgueres"};
        for (String cat : categories) {
            JButton chip = new JButton(cat);
            chip.setFont(UIPalette.FONT_LABEL.deriveFont(12f));
            chip.putClientProperty("JButton.buttonType", "roundRect");
            chip.addActionListener(e -> refreshTable(cat.equals("Todos") ? null : cat));
            filterPanel.add(chip);
        }
        northPanel.add(filterPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "NOME", "CATEGORIA", "PREÇO", "STATUS", "AÇÕES"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };
        refreshTable(null);

        table = new JTable(model);
        table.setRowHeight(60);
        
        // Setup Action Column
        TableActionCell actionCell = new TableActionCell(
            e -> JOptionPane.showMessageDialog(this, "Editar funcionalidade em breve!"),
            e -> {
                int row = table.getSelectedRow();
                String id = (String) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Excluir este produto?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    DataRepository.getInstance().deleteProduct(id);
                    refreshTable(null);
                }
            }
        );
        table.getColumnModel().getColumn(5).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(5).setCellEditor(actionCell);

        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);
        table.getTableHeader().setBackground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xF0F0F0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable(String categoryFilter) {
        model.setRowCount(0);
        List<Product> products = DataRepository.getInstance().getProducts();
        for (Product p : products) {
            if (categoryFilter == null || p.getCategory().equalsIgnoreCase(categoryFilter)) {
                model.addRow(new Object[]{
                    p.getId(), 
                    p.getName(), 
                    p.getCategory(), 
                    String.format("R$ %.2f", p.getPrice()), 
                    p.isActive() ? "ATIVO" : "INATIVO",
                    ""
                });
            }
        }
    }
}
