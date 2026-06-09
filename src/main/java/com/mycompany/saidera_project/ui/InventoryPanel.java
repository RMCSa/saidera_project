package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import java.util.List;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private DefaultTableModel model;
    private JPanel metricsWrapper;
    private List<StockItem> currentItems = new java.util.ArrayList<>();

    private JLabel title;
    private JLabel sub;
    private JTable table;
    private JScrollPane scrollPane;
    private ProgressBarRenderer progressBarRenderer;
    private StockActionCell stockActionCell;

    public InventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        title = new JLabel("Controle de Estoque");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        titlePanel.add(title);

        sub = new JLabel("Visão centralizada de insumos e barris.");
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
        
        table = new JTable(model) {
            private int hoveredRow = -1;
            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            repaint();
                        }
                    }
                });
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoveredRow = -1;
                        repaint();
                    }
                });
            }
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                boolean low = row >= 0 && row < currentItems.size() && currentItems.get(row).isLowStock();
                if (!isRowSelected(row)) {
                    c.setForeground(UIPalette.ON_BACKGROUND);
                    if (row == hoveredRow) {
                        c.setBackground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x334155) : new Color(0xF1F5F9));
                    } else if (low) {
                        c.setBackground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x450A0A) : new Color(0xFFF1F1));
                    } else if (row % 2 == 0) {
                        c.setBackground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x283548) : new Color(0xF8FAFC));
                    } else {
                        c.setBackground(UIPalette.SURFACE);
                    }
                }
                return c;
            }
        };
        table.setRowHeight(50);
        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);
        table.setFillsViewportHeight(true);

        refreshTable();

        // Custom Renderer for column 3: JProgressBar
        progressBarRenderer = new ProgressBarRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(progressBarRenderer);

        // Custom Renderer & Editor for Actions Column
        stockActionCell = new StockActionCell();
        table.getColumnModel().getColumn(4).setCellRenderer(stockActionCell);
        table.getColumnModel().getColumn(4).setCellEditor(stockActionCell);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        scrollPane = new JScrollPane(table);
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
        card.setBackground(UIPalette.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(15, 20, 15, 20)));

        // Circle icon
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = com.formdev.flatlaf.FlatLaf.isLafDark() ? getDarkIconBg(iconBg) : iconBg;
                g2.setColor(bg);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);
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
        t.setForeground(UIPalette.TEXT_SECONDARY);
        textContainer.add(t);

        JLabel v = new JLabel(val);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(26f));
        v.setForeground(accentColor);
        textContainer.add(v);

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        s.setForeground(UIPalette.TEXT_SECONDARY);
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

        public void updateUI() {
            if (panel != null) panel.updateUI();
            if (plusBtn != null) plusBtn.updateUI();
            if (minusBtn != null) minusBtn.updateUI();
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

    private static class ProgressBarRenderer implements javax.swing.table.TableCellRenderer {

        // Painel que desenha a barra de progresso manualmente via Graphics2D.
        // Isso evita que o FlatLaf sobrescreva as cores, pois setBackground/setForeground
        // em JProgressBar são ignorados pelo LAF.
        private static final class PaintPanel extends JPanel {
            Color trackBg   = Color.LIGHT_GRAY;
            Color fillColor = UIPalette.SUCCESS;
            int   percent   = 0;
            String label    = "";

            PaintPanel() {
                super(null);
                setOpaque(true);
            }

            void update(int pct, Color fill, Color track, Color bg, String lbl) {
                this.percent   = pct;
                this.fillColor = fill;
                this.trackBg   = track;
                this.label     = lbl;
                setBackground(bg);
            }

            @Override
            protected void paintComponent(Graphics g) {
                // 1. Background da linha
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 2. Área da barra com padding
                int pad  = 10;
                int barH = 16;
                int x    = pad;
                int y    = (getHeight() - barH) / 2;
                int w    = getWidth() - pad * 2;
                if (w <= 0) { g2.dispose(); return; }

                // 3. Track
                g2.setColor(trackBg);
                g2.fillRoundRect(x, y, w, barH, barH, barH);

                // 4. Fill
                int fillW = (int) (w * Math.min(percent, 100) / 100.0);
                if (fillW > 0) {
                    g2.setColor(fillColor);
                    g2.fillRoundRect(x, y, fillW, barH, barH, barH);
                }

                // 5. Label centralizado
                g2.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
                FontMetrics fm = g2.getFontMetrics();
                // escolhe contraste: branco sobre track escuro, escuro sobre track claro
                float brightness = (trackBg.getRed() * 0.299f + trackBg.getGreen() * 0.587f + trackBg.getBlue() * 0.114f) / 255f;
                g2.setColor(brightness < 0.5f ? Color.WHITE : new Color(0x1E293B));
                int tx = x + (w - fm.stringWidth(label)) / 2;
                int ty = y + (barH + fm.getAscent() - fm.getDescent()) / 2 - 1;
                g2.drawString(label, tx, ty);

                g2.dispose();
            }
        }

        private final PaintPanel panel = new PaintPanel();

        public void updateUI() {
            panel.updateUI();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (value instanceof StockItem) {
                StockItem item = (StockItem) value;
                int cur  = item.getCurrentLevel();
                int min  = item.getMinimumLevel();
                int max  = Math.max(min * 2, 10);
                int pct  = Math.min(100, (int) (((double) cur / Math.max(1, max)) * 100));

                // Cor do fill
                Color fill;
                if (item.isLowStock()) {
                    fill = UIPalette.ERROR;
                } else if (cur < min * 1.5) {
                    fill = UIPalette.WARNING;
                } else {
                    fill = UIPalette.SUCCESS;
                }

                // Track
                boolean dark  = com.formdev.flatlaf.FlatLaf.isLafDark();
                Color track   = dark ? new Color(0x3A4A5A) : new Color(0xDDE3EA);

                // Background da linha
                Color bg;
                if (isSelected) {
                    bg = table.getSelectionBackground();
                } else if (item.isLowStock()) {
                    bg = dark ? new Color(0x450A0A) : new Color(0xFFF1F1);
                } else if (row % 2 == 0) {
                    bg = dark ? new Color(0x283548) : new Color(0xF8FAFC);
                } else {
                    bg = UIPalette.SURFACE;
                }

                String lbl = cur + " / " + min + " " + item.getUnit();
                panel.update(pct, fill, track, bg, lbl);
                return panel;
            }
            return new JLabel(value != null ? value.toString() : "");
        }
    }

    private Color getDarkIconBg(Color lightBg) {
        if (lightBg.equals(new Color(0xEFF6FF))) return new Color(0x1E3A8A); // blue
        if (lightBg.equals(new Color(0xECFDF5))) return new Color(0x064E3B); // green
        if (lightBg.equals(new Color(0xFEF2F2))) return new Color(0x7F1D1D); // red
        if (lightBg.equals(new Color(0xFFFBEB))) return new Color(0x78350F); // amber
        return lightBg;
    }

    public void updateThemeColors() {
        setBackground(UIPalette.BACKGROUND);
        if (title != null) title.setForeground(UIPalette.ON_BACKGROUND);
        if (sub != null) sub.setForeground(UIPalette.TEXT_SECONDARY);

        // Recria as métricas no topo com o tema correto
        refreshTable();

        // Atualiza os renderers
        if (progressBarRenderer != null) {
            progressBarRenderer.updateUI();
        }
        if (stockActionCell != null) {
            stockActionCell.updateUI();
        }

        // Atualiza a tabela
        if (table != null) {
            table.setBackground(UIPalette.SURFACE);
            table.setForeground(UIPalette.ON_BACKGROUND);
            table.getTableHeader().setBackground(UIPalette.SURFACE);
            table.getTableHeader().setForeground(UIPalette.ON_BACKGROUND);
            table.setGridColor(UIPalette.BORDER);
        }
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
            scrollPane.getViewport().setBackground(UIPalette.SURFACE);
        }

        repaint();
    }
}
