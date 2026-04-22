package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JPanel createChartPanel() {
        org.jfree.data.category.DefaultCategoryDataset dataset = new org.jfree.data.category.DefaultCategoryDataset();
        DataRepository repo = DataRepository.getInstance();
        for (StockItem item : repo.getInventory()) {
            dataset.addValue(item.getCurrentLevel(), "Atual", item.getName());
            dataset.addValue(item.getMinimumLevel(), "Mínimo", item.getName());
        }

        org.jfree.chart.JFreeChart barChart = org.jfree.chart.ChartFactory.createBarChart(
                "Nível Atual vs Nível Mínimo",
                "", "Quantidade",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false);

        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setRangeGridlinePaint(new Color(0xE0E0E0));

        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) barChart
                .getCategoryPlot().getRenderer();
        renderer.setSeriesPaint(0, UIPalette.AMBER);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);

        org.jfree.chart.ChartPanel panel = new org.jfree.chart.ChartPanel(barChart);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        return panel;
    }

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Visão Geral");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        header.add(title, BorderLayout.WEST);

        JButton exportBtn = new JButton("Exportar Dados");
        exportBtn.setFont(UIPalette.FONT_LABEL);
        exportBtn.setEnabled(false);
        exportBtn.setToolTipText("Disponível apenas na versão completa (demo). ");
        header.add(exportBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Main Dashboard Body (Left: KPIs + Chart, Right: Shortcuts)
        JPanel body = new JPanel(new BorderLayout(30, 0));
        body.setOpaque(false);

        // LEFT COLUMN
        JPanel leftCol = new JPanel(new GridBagLayout());
        leftCol.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 30, 0);

        // KPI Row
        DataRepository repo = DataRepository.getInstance();
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createCard("Itens no Cardápio", String.format("%02d", repo.getProducts().size()),
            "Produtos cadastrados", Color.BLACK));
        kpiPanel.add(createCard("Produtos Ativos", String.format("%02d", repo.getActiveProductCount()),
            "Disponíveis para venda", UIPalette.SUCCESS));
        kpiPanel.add(createCard("Alertas Baixo Estoque", String.format("%02d", repo.getLowStockCount()),
            "Itens abaixo do mínimo", UIPalette.ERROR));
        kpiPanel.add(createCard("Itens em Estoque", String.format("%02d", repo.getInventory().size()),
            "Insumos cadastrados", UIPalette.AMBER_DARK));

        gbc.gridy = 0;
        gbc.weighty = 0.2;
        leftCol.add(kpiPanel, gbc);

        // Chart
        gbc.gridy = 1;
        gbc.weighty = 0.8;
        leftCol.add(createChartPanel(), gbc);

        body.add(leftCol, BorderLayout.CENTER);

        // RIGHT COLUMN: Repository-driven summary
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setPreferredSize(new Dimension(300, 0));
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));

        JLabel shortcutTitle = new JLabel("Resumo do Mock");
        shortcutTitle.setFont(UIPalette.FONT_TITLE);
        shortcutTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        rightCol.add(shortcutTitle);

        rightCol.add(createShortcut("Produtos ativos", String.format("%02d itens", repo.getActiveProductCount()), "🍻"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(createShortcut("Produtos inativos", String.format("%02d itens", repo.getInactiveProductCount()), "⏸"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(createShortcut("Produtos vinculados", String.format("%02d itens", repo.getLinkedProductCount()), "🔗"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(createShortcut("Categorias cadastradas", String.format("%02d categorias", repo.getProductCategories().size()), "🏷️"));

        body.add(rightCol, BorderLayout.EAST);

        // SOUTH: Critical Alerts Table
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        JLabel alertTitle = new JLabel("Produtos em Alerta Crítico");
        alertTitle.setFont(UIPalette.FONT_TITLE);
        alertTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        southPanel.add(alertTitle, BorderLayout.NORTH);

        String[] colNames = { "PRODUTO", "NÍVEL ATUAL", "NÍVEL MÍNIMO", "STATUS" };
        DefaultTableModel alertModel = new DefaultTableModel(colNames, 0);
        for (StockItem item : repo.getInventory()) {
            if (item.isLowStock()) {
                alertModel.addRow(new Object[] { item.getName(), item.getCurrentLevel() + " " + item.getUnit(),
                        item.getMinimumLevel() + " " + item.getUnit(), "ABAIXO DO MÍNIMO" });
            }
        }

        JTable alertTable = new JTable(alertModel);
        alertTable.setRowHeight(40);
        alertTable.setFont(UIPalette.FONT_BODY);
        JScrollPane alertScroll = new JScrollPane(alertTable);
        alertScroll.setPreferredSize(new Dimension(0, 150));
        alertScroll.getViewport().setBackground(Color.WHITE);

        JPanel emptyState = new JPanel(new BorderLayout());
        emptyState.setBackground(Color.WHITE);
        emptyState.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                new EmptyBorder(30, 20, 30, 20)));
        JLabel emptyLabel = new JLabel("Nenhum alerta crítico hoje.");
        emptyLabel.setFont(UIPalette.FONT_BODY);
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyState.add(emptyLabel, BorderLayout.CENTER);

        if (alertModel.getRowCount() == 0) {
            southPanel.add(emptyState, BorderLayout.CENTER);
        } else {
            southPanel.add(alertScroll, BorderLayout.CENTER);
        }

        body.add(southPanel, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);
    }

    private JPanel createShortcut(String title, String sub, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(300, 100));

        JLabel ic = new JLabel(icon);
        ic.setFont(UIPalette.FONT_TITLE.deriveFont(24f));
        card.add(ic, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(UIPalette.FONT_LABEL);
        text.add(t);
        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        s.setForeground(Color.GRAY);
        text.add(s);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(String title, String value, String sub, Color valueColor) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel t = new JLabel(title);
        t.setFont(UIPalette.FONT_LABEL);
        t.setForeground(UIPalette.TEXT_SECONDARY);
        gbc.gridy = 0;
        card.add(t, gbc);

        JLabel v = new JLabel(value);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(28f));
        v.setForeground(valueColor);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        card.add(v, gbc);

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN));
        s.setForeground(valueColor == UIPalette.ERROR ? UIPalette.ERROR : Color.GRAY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(s, gbc);

        return card;
    }
}
