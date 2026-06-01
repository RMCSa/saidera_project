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
    private JPanel metricsWrapper;
    private List<StockItem> currentItems = new ArrayList<>();

    public InventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40));

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
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Metric cards wrapper
        metricsWrapper = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsWrapper.setOpaque(false);
        centerPanel.add(metricsWrapper, BorderLayout.NORTH);

        // Table Model Setup
        String[] columns = { "PRODUTO", "NÍVEL ATUAL", "MÍNIMO", "NÍVEL DE ESTOQUE", "AÇÕES" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column is interactive
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);

        // Custom Renderer for default columns
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

        refreshTable();

        // Custom Renderer for column 3: JProgressBar
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private final JProgressBar bar = new JProgressBar(0, 100);
            {
                bar.setStringPainted(true);
                bar.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (value instanceof StockItem) {
                    StockItem item = (StockItem) value;
                    int cur = item.getCurrentLevel();
                    int min = item.getMinimumLevel();
                    int max = Math.max(min * 2, 10);
                    int pct = Math.min(100, (int) (((double) cur / Math.max(1, max)) * 100));
                    
                    bar.setValue(pct);
                    bar.setString(cur + " / " + min + " " + item.getUnit());
                    
                    if (item.isLowStock()) {
                        bar.setForeground(UIPalette.ERROR);
                    } else if (cur < min * 1.5) {
                        bar.setForeground(UIPalette.WARNING);
                    } else {
                        bar.setForeground(UIPalette.SUCCESS);
                    }
                    
                    if (isSelected) {
                        bar.setBackground(table.getSelectionBackground());
                    } else {
                        bar.setBackground(item.isLowStock() ? new Color(0xFFF1F1) : Color.WHITE);
                    }
                    return bar;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Custom Renderer & Editor for Actions Column
        StockActionCell actionCell = new StockActionCell();
        table.getColumnModel().getColumn(4).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(4).setCellEditor(actionCell);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshTable() {
        model.setRowCount(0);
        List<StockItem> items = DataRepository.getInstance().getInventory();
        
        // Sort: low stock items first
        items.sort((a, b) -> Boolean.compare(b.isLowStock(), a.isLowStock()));
        currentItems = items;

        for (StockItem item : items) {
            model.addRow(new Object[] {
                    item.getName(),
                    String.format("%02d %s", item.getCurrentLevel(), item.getUnit()),
                    String.format("%02d %s", item.getMinimumLevel(), item.getUnit()),
                    item, // progress bar cell data
                    item  // action cell data
            });
        }

        int count = DataRepository.getInstance().getLowStockCount();
        int total = items.size();
        int stable = total - count;

        if (metricsWrapper != null) {
            metricsWrapper.removeAll();
            metricsWrapper.add(createMetricCard("Alertas Críticos", String.format("%02d", count), "Abaixo da reserva", UIPalette.ERROR, "⚠️", new Color(0xFEF2F2)));
            metricsWrapper.add(createMetricCard("Itens Estáveis", String.format("%02d", stable), "Estoque seguro", UIPalette.SUCCESS, "✅", new Color(0xECFDF5)));
            metricsWrapper.add(createMetricCard("Total Insumos", String.format("%02d", total), "Cadastrados", UIPalette.AMBER_DARK, "📦", new Color(0xFFFBEB)));
            metricsWrapper.revalidate();
            metricsWrapper.repaint();
        }
    }

    private JPanel createMetricCard(String title, String val, String sub, Color accentColor, String iconText, Color iconBg) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(15, 20, 15, 20)));

        // Circle icon
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(42, 42));
        iconCircle.setOpaque(false);
        JLabel iconLbl = new JLabel(iconText);
        iconLbl.setFont(UIPalette.FONT_TITLE.deriveFont(18f));
        iconCircle.add(iconLbl);
        card.add(iconCircle, BorderLayout.WEST);

        // Texts Container
        JPanel textContainer = new JPanel();
        textContainer.setOpaque(false);
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        t.setForeground(Color.GRAY);
        textContainer.add(t);

        JLabel v = new JLabel(val);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(26f));
        v.setForeground(accentColor);
        textContainer.add(v);

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        s.setForeground(Color.GRAY);
        textContainer.add(s);

        card.add(textContainer, BorderLayout.CENTER);
        return card;
    }

    // Custom Cell Editor and Renderer for Row-level actions
    private class StockActionCell extends AbstractCellEditor implements javax.swing.table.TableCellRenderer, javax.swing.table.TableCellEditor {
        private final JPanel panel;
        private final JButton plusBtn;
        private final JButton minusBtn;
        private StockItem currentItem;

        public StockActionCell() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setOpaque(false);

            plusBtn = new JButton("+");
            plusBtn.setFont(UIPalette.FONT_LABEL.deriveFont(12f));
            plusBtn.setBackground(UIPalette.SUCCESS);
            plusBtn.setForeground(Color.WHITE);
            plusBtn.setPreferredSize(new Dimension(38, 26));
            plusBtn.setFocusPainted(false);
            plusBtn.setToolTipText("Registrar entrada rápida");
            plusBtn.addActionListener(e -> {
                fireEditingStopped();
                Window owner = SwingUtilities.getWindowAncestor(InventoryPanel.this);
                new StockTransactionForm((Frame) owner, true, InventoryPanel.this::refreshTable, currentItem).setVisible(true);
            });

            minusBtn = new JButton("-");
            minusBtn.setFont(UIPalette.FONT_LABEL.deriveFont(12f));
            minusBtn.setBackground(UIPalette.ERROR);
            minusBtn.setForeground(Color.WHITE);
            minusBtn.setPreferredSize(new Dimension(38, 26));
            minusBtn.setFocusPainted(false);
            minusBtn.setToolTipText("Registrar saída rápida");
            minusBtn.addActionListener(e -> {
                fireEditingStopped();
                Window owner = SwingUtilities.getWindowAncestor(InventoryPanel.this);
                new StockTransactionForm((Frame) owner, false, InventoryPanel.this::refreshTable, currentItem).setVisible(true);
            });

            panel.add(plusBtn);
            panel.add(minusBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.currentItem = (StockItem) value;
            return panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentItem = (StockItem) value;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentItem;
        }

        @Override
        public boolean isCellEditable(java.util.EventObject e) {
            return true;
        }
    }
}
