package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import java.util.List;
import java.awt.*;

public class InventoryPanel extends JPanel {

    public InventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Controle de Estoque");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        titlePanel.add(title);
        
        JLabel sub = new JLabel("Visão centralizada de insumos e barris.");
        sub.setFont(UIPalette.FONT_BODY);
        sub.setForeground(UIPalette.TEXT_SECONDARY);
        titlePanel.add(sub);
        
        header.add(titlePanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        JButton outBtn = new JButton("Registrar Saída");
        JButton inBtn = new JButton("Registrar Entrada");
        inBtn.setBackground(UIPalette.AMBER);
        btnPanel.add(outBtn);
        btnPanel.add(inBtn);
        header.add(btnPanel, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // Alerts and Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 30));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        // Alert Area
        JPanel alertPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        alertPanel.setOpaque(false);
        
        int lowStockCount = DataRepository.getInstance().getLowStockCount();
        JPanel criticalAlert = new JPanel(new BorderLayout(20, 0));
        criticalAlert.setBackground(new Color(0xFFF1F1));
        criticalAlert.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFFCDD2)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel alertT = new JLabel(String.format("%02d Itens Críticos", lowStockCount));
        alertT.setFont(UIPalette.FONT_TITLE);
        alertT.setForeground(UIPalette.ERROR);
        criticalAlert.add(alertT, BorderLayout.NORTH);
        JLabel alertS = new JLabel("Insumos abaixo do nível de reserva.");
        alertS.setFont(UIPalette.FONT_BODY);
        criticalAlert.add(alertS, BorderLayout.CENTER);
        
        alertPanel.add(criticalAlert);
        alertPanel.add(new JPanel()); // Empty spacer for now

        centerPanel.add(alertPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"PRODUTO", "NÍVEL ATUAL", "NÍVEL MÍNIMO", "STATUS"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<StockItem> items = DataRepository.getInstance().getInventory();
        for (StockItem item : items) {
            model.addRow(new Object[]{
                item.getName(),
                String.format("%02d %s", item.getCurrentLevel(), item.getUnit()),
                String.format("%02d %s", item.getMinimumLevel(), item.getUnit()),
                item.isLowStock() ? "ABAIXO DO MÍNIMO" : "ESTÁVEL"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }
}
