package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private DefaultTableModel model;
    private JLabel alertT;
    private JLabel summaryLabel;
    private List<StockItem> currentItems = new ArrayList<>();

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
        outBtn.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new StockTransactionForm((Frame) owner, false, this::refreshTable).setVisible(true);
        });

        JButton inBtn = new JButton("Registrar Entrada");
        inBtn.setBackground(UIPalette.AMBER);
        inBtn.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new StockTransactionForm((Frame) owner, true, this::refreshTable).setVisible(true);
        });

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

        alertT = new JLabel();
        alertT.setFont(UIPalette.FONT_TITLE);
        alertT.setForeground(UIPalette.ERROR);

        JPanel criticalAlert = new JPanel(new BorderLayout(20, 0));
        criticalAlert.setBackground(new Color(0xFFF1F1));
        criticalAlert.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFFCDD2)),
                new EmptyBorder(20, 20, 20, 20)));
        criticalAlert.add(alertT, BorderLayout.NORTH);
        JLabel alertS = new JLabel("Insumos abaixo do nível de reserva.");
        alertS.setFont(UIPalette.FONT_BODY);
        criticalAlert.add(alertS, BorderLayout.CENTER);

        alertPanel.add(criticalAlert);

        JPanel summaryCard = new JPanel(new BorderLayout(10, 5));
        summaryCard.setBackground(Color.WHITE);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel sumTitle = new JLabel("RESUMO");
        sumTitle.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        sumTitle.setForeground(Color.GRAY);
        summaryCard.add(sumTitle, BorderLayout.NORTH);

        summaryLabel = new JLabel();
        summaryLabel.setFont(UIPalette.FONT_BODY);
        summaryLabel.setForeground(UIPalette.ON_BACKGROUND);
        summaryCard.add(summaryLabel, BorderLayout.CENTER);

        alertPanel.add(summaryCard);

        centerPanel.add(alertPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "PRODUTO", "NÍVEL ATUAL", "NÍVEL MÍNIMO", "STATUS" };
        model = new DefaultTableModel(columns, 0);
        refreshTable();

        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean low = row >= 0 && row < currentItems.size() && currentItems.get(row).isLowStock();
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setForeground(UIPalette.ON_BACKGROUND);
                    c.setBackground(low ? new Color(0xFFF1F1) : Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        model.setRowCount(0);
        currentItems = DataRepository.getInstance().getInventory();
        List<StockItem> items = currentItems;
        for (StockItem item : items) {
            model.addRow(new Object[] {
                    item.getName(),
                    String.format("%02d %s", item.getCurrentLevel(), item.getUnit()),
                    String.format("%02d %s", item.getMinimumLevel(), item.getUnit()),
                    item.isLowStock() ? "ABAIXO DO MÍNIMO" : "ESTÁVEL"
            });
        }

        int count = DataRepository.getInstance().getLowStockCount();
        alertT.setText(String.format("%02d Itens Críticos", count));

        int total = items.size();
        int stable = total - count;
        if (summaryLabel != null) {
            summaryLabel.setText(String.format("%02d itens • %02d estáveis", total, stable));
        }
    }
}
