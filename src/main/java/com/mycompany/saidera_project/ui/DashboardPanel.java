package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JLabel title;
    private JLabel shortcutTitle;
    private JPanel cardContainer;
    private JLabel alertTitle;
    private JTable alertTable;
    private JScrollPane alertScroll;
    private JPanel emptyState;
    private JLabel emptyLabel;
    private JLabel warningBannerText;
    
    private final java.util.List<JPanel> kpiCards = new java.util.ArrayList<>();
    private final java.util.List<JPanel> shortcutCards = new java.util.ArrayList<>();

    private org.knowm.xchart.CategoryChart chart;
    private org.knowm.xchart.XChartPanel<org.knowm.xchart.CategoryChart> chartPanel;

    private JPanel createChartPanel() {
        DataRepository repo = DataRepository.getInstance();

        // Coleta de dados para o gráfico
        String[] labels = repo.getInventory().stream()
                .map(item -> {
                    String name = item.getName();
                    if (name.length() > 15) {
                        return name.substring(0, 12) + "...";
                    }
                    return name;
                })
                .toArray(String[]::new);
        double[] atual = repo.getInventory().stream()
                .mapToDouble(item -> item.getCurrentLevel())
                .toArray();
        double[] minimo = repo.getInventory().stream()
                .mapToDouble(item -> item.getMinimumLevel())
                .toArray();

        // Criação do gráfico com XChart
        chart = new org.knowm.xchart.CategoryChartBuilder()
                .title("Nível Atual vs Nível Mínimo")
                .xAxisTitle("")
                .yAxisTitle("Quantidade")
                .theme(org.knowm.xchart.style.Styler.ChartTheme.GGPlot2)
                .build();

        // Estilo do gr\u00e1fico
        org.knowm.xchart.style.CategoryStyler styler = chart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setPlotBorderVisible(false);
        styler.setChartFontColor(new Color(0x091D2E));
        styler.setAxisTickLabelsFont(UIPalette.FONT_LABEL.deriveFont(10f));
        styler.setAxisTitleFont(UIPalette.FONT_LABEL.deriveFont(12f));
        styler.setChartTitleFont(UIPalette.FONT_TITLE.deriveFont(Font.BOLD, 14f));
        styler.setChartTitleVisible(true);
        styler.setChartTitleBoxVisible(false);
        styler.setLegendVisible(true);
        styler.setLegendPosition(org.knowm.xchart.style.Styler.LegendPosition.InsideNE);
        styler.setLegendFont(UIPalette.FONT_LABEL.deriveFont(10f));
        styler.setLegendBorderColor(UIPalette.BORDER);
        styler.setLegendBackgroundColor(Color.WHITE);
        styler.setXAxisLabelRotation(45);
        styler.setAvailableSpaceFill(0.8);
        styler.setOverlapped(false);
        styler.setPlotGridLinesColor(new Color(0xE2E8F0));
        styler.setPlotGridLinesStroke(new java.awt.BasicStroke(0.5f));

        // Cores das s\u00e9ries
        styler.setSeriesColors(new Color[]{UIPalette.AMBER, new Color(0xCBD5E1)});

        // Adiciona as s\u00e9ries
        if (labels.length > 0) {
            java.util.List<String> labelList = java.util.Arrays.asList(labels);
            java.util.List<Number> atualList = new java.util.ArrayList<>();
            java.util.List<Number> minimoList = new java.util.ArrayList<>();
            for (double v : atual) atualList.add(v);
            for (double v : minimo) minimoList.add(v);
            chart.addSeries("Atual", labelList, atualList);
            chart.addSeries("M\u00ednimo", labelList, minimoList);
        } else {
            // Estado vazio: adiciona um ponto placeholder
            chart.addSeries("Atual", java.util.Arrays.asList("Sem dados"), java.util.Arrays.asList(0));
            chart.addSeries("M\u00ednimo", java.util.Arrays.asList("Sem dados"), java.util.Arrays.asList(0));
        }

        // Painel do gráfico
        chartPanel = new org.knowm.xchart.XChartPanel<>(chart);
        chartPanel.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
        return chartPanel;
    }

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        header.setOpaque(false);
        title = new JLabel("Visão Geral");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        header.add(title);

        JButton exportBtn = new JButton("Exportar Dados");
        exportBtn.setFont(UIPalette.FONT_LABEL);
        exportBtn.setEnabled(false);
        exportBtn.setToolTipText("Disponível apenas na versão completa (demo).");
        header.add(exportBtn);

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
        gbc.insets = new Insets(0, 0, 20, 0);

        DataRepository repo = DataRepository.getInstance();
        int lowStockCount = repo.getLowStockCount();
        int gridY = 0;

        // Warning Banner
        if (lowStockCount > 0) {
            JPanel banner = new JPanel(new BorderLayout(15, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x450A0A) : new Color(0xFEF2F2)); // dark red or light red bg
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(UIPalette.ERROR);
                    g2.fillRect(0, 0, 5, getHeight()); // left accent border
                    g2.dispose();
                }
            };
            banner.setBorder(new EmptyBorder(12, 20, 12, 20));
            
            warningBannerText = new JLabel("<html><b>Atenção:</b> Existem <b>" + lowStockCount + "</b> itens com estoque crítico. Verifique o estoque para reabastecimento.</html>");
            warningBannerText.setFont(UIPalette.FONT_BODY);
            warningBannerText.setForeground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0xFECACA) : UIPalette.ERROR);
            banner.add(warningBannerText, BorderLayout.CENTER);

            JButton actionBtn = new JButton("Ir para Estoque");
            actionBtn.setBackground(UIPalette.ERROR);
            actionBtn.setForeground(Color.WHITE);
            actionBtn.setFont(UIPalette.FONT_LABEL.deriveFont(11f));
            actionBtn.setFocusPainted(false);
            actionBtn.addActionListener(e -> {
                Window w = SwingUtilities.getWindowAncestor(DashboardPanel.this);
                if (w instanceof MainFrame) {
                    ((MainFrame) w).showPanel("Inventory");
                }
            });
            banner.add(actionBtn, BorderLayout.EAST);

            gbc.gridy = gridY++;
            gbc.weighty = 0.05;
            leftCol.add(banner, gbc);
        }

        // KPI Row
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.add(addKpiCard("Itens no Cardápio", String.format("%02d", repo.getProducts().size()),
            "Cadastrados", UIPalette.ON_BACKGROUND, "🍴", new Color(0xEFF6FF)));
        kpiPanel.add(addKpiCard("Produtos Ativos", String.format("%02d", repo.getActiveProductCount()),
            "Disponíveis", UIPalette.SUCCESS, "✅", new Color(0xECFDF5)));
        kpiPanel.add(addKpiCard("Alertas Críticos", String.format("%02d", lowStockCount),
            "Abaixo do mínimo", UIPalette.ERROR, "⚠️", new Color(0xFEF2F2)));
        kpiPanel.add(addKpiCard("Itens em Estoque", String.format("%02d", repo.getInventory().size()),
            "Insumos", UIPalette.AMBER_DARK, "📦", new Color(0xFFFBEB)));

        gbc.gridy = gridY++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftCol.add(kpiPanel, gbc);

        // Chart
        gbc.gridy = gridY++;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        leftCol.add(createChartPanel(), gbc);

        body.add(leftCol, BorderLayout.CENTER);

        // RIGHT COLUMN: Shortcuts
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setPreferredSize(new Dimension(220, 0));
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));

        shortcutTitle = new JLabel("Resumo da Choperia");
        shortcutTitle.setFont(UIPalette.FONT_TITLE.deriveFont(15f));
        shortcutTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        shortcutTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        rightCol.add(shortcutTitle);

        rightCol.add(addShortcutCard("Produtos ativos", String.format("%02d itens", repo.getActiveProductCount()), "🍻"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(addShortcutCard("Produtos inativos", String.format("%02d itens", repo.getInactiveProductCount()), "⏸"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(addShortcutCard("Produtos vinculados", String.format("%02d itens", repo.getLinkedProductCount()), "🔗"));
        rightCol.add(Box.createVerticalStrut(15));
        rightCol.add(addShortcutCard("Categorias", String.format("%02d cadastradas", repo.getProductCategories().size()), "🏷️"));

        body.add(rightCol, BorderLayout.EAST);

        // SOUTH: Critical Alerts Table wrapped in a card
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        cardContainer = new JPanel(new BorderLayout());
        cardContainer.setBackground(Color.WHITE);
        cardContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        alertTitle = new JLabel("Produtos em Alerta Crítico");
        alertTitle.setFont(UIPalette.FONT_TITLE.deriveFont(16f));
        alertTitle.setForeground(UIPalette.ON_BACKGROUND);
        alertTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        cardContainer.add(alertTitle, BorderLayout.NORTH);

        String[] colNames = { "PRODUTO", "NÍVEL ATUAL", "NÍVEL MÍNIMO", "STATUS" };
        DefaultTableModel alertModel = new DefaultTableModel(colNames, 0);
        for (StockItem item : repo.getInventory()) {
            if (item.isLowStock()) {
                alertModel.addRow(new Object[] { item.getName(), item.getCurrentLevel() + " " + item.getUnit(),
                        item.getMinimumLevel() + " " + item.getUnit(), "ABAIXO DO MÍNIMO" });
            }
        }

        alertTable = new JTable(alertModel) {
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
                if (!isRowSelected(row)) {
                    if (row == hoveredRow) {
                        c.setBackground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x334155) : new Color(0xF1F5F9));
                    } else if (row % 2 == 0) {
                        c.setBackground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x283548) : new Color(0xF8FAFC));
                    } else {
                        c.setBackground(UIPalette.SURFACE);
                    }
                }
                return c;
            }
        };
        alertTable.setRowHeight(40);
        alertTable.setFont(UIPalette.FONT_BODY);
        alertTable.getTableHeader().setFont(UIPalette.FONT_LABEL);
        alertTable.getTableHeader().setBackground(Color.WHITE);
        alertTable.setShowVerticalLines(false);
        alertTable.setGridColor(UIPalette.BORDER);
        alertTable.setFillsViewportHeight(true);

        // Renderer customizado com badge semântico na coluna STATUS
        alertTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            private final JPanel badge = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x450A0A) : new Color(0xFEF2F2)); // dark red or light red bg
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x7F1D1D) : new Color(0xFECACA)); // dark red or light red border
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
            };
            private final JLabel statusLabel = new JLabel();
            {
                badge.setOpaque(false);
                badge.setLayout(new GridBagLayout());
                badge.setPreferredSize(new Dimension(140, 22));
                statusLabel.setFont(UIPalette.FONT_LABEL.deriveFont(9f));
                statusLabel.setForeground(UIPalette.ERROR);
                badge.add(statusLabel);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 9));
                wrapper.setOpaque(true);
                if (value != null) {
                    statusLabel.setText(value.toString());
                    statusLabel.setForeground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0xFECACA) : UIPalette.ERROR);
                    wrapper.add(badge);
                }
                if (isSelected) {
                    wrapper.setBackground(table.getSelectionBackground());
                } else {
                    wrapper.setBackground(table.getBackground());
                }
                return wrapper;
            }
        });

        alertScroll = new JScrollPane(alertTable);
        alertScroll.setPreferredSize(new Dimension(0, 150));
        alertScroll.getViewport().setBackground(Color.WHITE);
        alertScroll.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));

        emptyState = new JPanel(new BorderLayout());
        emptyState.setBackground(Color.WHITE);
        emptyState.setBorder(new EmptyBorder(20, 20, 20, 20));
        emptyLabel = new JLabel("Nenhum alerta crítico hoje.");
        emptyLabel.setFont(UIPalette.FONT_BODY);
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyState.add(emptyLabel, BorderLayout.CENTER);

        if (alertModel.getRowCount() == 0) {
            cardContainer.add(emptyState, BorderLayout.CENTER);
        } else {
            cardContainer.add(alertScroll, BorderLayout.CENTER);
        }

        southPanel.add(cardContainer, BorderLayout.CENTER);
        body.add(southPanel, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);
    }

    private JPanel createShortcut(String title, String sub, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(220, 68));
        card.setPreferredSize(new Dimension(220, 68));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF8FAFC)); // slate-50
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(42, 42));
        iconCircle.setOpaque(false);
        JLabel ic = new JLabel(icon);
        ic.setFont(UIPalette.FONT_TITLE.deriveFont(18f));
        iconCircle.add(ic);
        card.add(iconCircle, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 11f));
        t.setForeground(new Color(0x64748B)); // Slate-500
        text.add(t);

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_TITLE.deriveFont(16f));
        s.setForeground(UIPalette.ON_BACKGROUND);
        text.add(s);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(String title, String value, String sub, Color valueColor, String iconText, Color iconBg) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(15, 15, 15, 15)));

        // Circle Icon container
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color lBg = (Color) getClientProperty("iconBg");
                Color bg = com.formdev.flatlaf.FlatLaf.isLafDark() ? getDarkIconBg(lBg) : lBg;
                g2.setColor(bg);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);
                g2.dispose();
            }
        };
        iconCircle.putClientProperty("iconBg", iconBg);
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

        JLabel t = new JLabel(title);
        t.setFont(UIPalette.FONT_LABEL.deriveFont(11f));
        t.setForeground(UIPalette.TEXT_SECONDARY);
        textContainer.add(t);

        JLabel v = new JLabel(value);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(26f));
        v.setForeground(valueColor);
        textContainer.add(v);

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        if (valueColor == UIPalette.ERROR) {
            s.setForeground(UIPalette.ERROR);
        } else if (valueColor == UIPalette.SUCCESS) {
            s.setForeground(UIPalette.SUCCESS);
        } else {
            s.setForeground(new Color(0x64748B));
        }
        textContainer.add(s);

        card.add(textContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel addKpiCard(String title, String value, String sub, Color valueColor, String iconText, Color iconBg) {
        JPanel card = createCard(title, value, sub, valueColor, iconText, iconBg);
        kpiCards.add(card);
        return card;
    }

    private JPanel addShortcutCard(String title, String sub, String icon) {
        JPanel card = createShortcut(title, sub, icon);
        shortcutCards.add(card);
        return card;
    }

    private Color getDarkIconBg(Color lightBg) {
        if (lightBg.equals(new Color(0xEFF6FF))) return new Color(0x1E3A8A); // blue
        if (lightBg.equals(new Color(0xECFDF5))) return new Color(0x064E3B); // green
        if (lightBg.equals(new Color(0xFEF2F2))) return new Color(0x7F1D1D); // red
        if (lightBg.equals(new Color(0xFFFBEB))) return new Color(0x78350F); // amber
        return lightBg;
    }

    private void updateCardStyle(JPanel card, Color valueColor) {
        card.setBackground(UIPalette.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(15, 15, 15, 15)));
        
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getLayout() instanceof GridBagLayout) { // iconCircle
                    p.repaint();
                } else if (p.getLayout() instanceof BoxLayout) { // textContainer
                    for (Component tc : p.getComponents()) {
                        if (tc instanceof JLabel) {
                            JLabel lbl = (JLabel) tc;
                            if (lbl.getFont().getSize() == 11) { // title label
                                lbl.setForeground(UIPalette.TEXT_SECONDARY);
                            } else if (lbl.getFont().getSize() == 26) { // value label
                                // mantém a cor original (valueColor)
                            } else { // subtitle label
                                if (valueColor == UIPalette.ERROR) {
                                    lbl.setForeground(UIPalette.ERROR);
                                } else if (valueColor == UIPalette.SUCCESS) {
                                    lbl.setForeground(UIPalette.SUCCESS);
                                } else {
                                    lbl.setForeground(UIPalette.TEXT_SECONDARY);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateShortcutStyle(JPanel card) {
        card.setBackground(UIPalette.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getLayout() instanceof GridBagLayout) { // iconCircle
                    p.repaint();
                } else if (p.getLayout() instanceof BoxLayout) { // textContainer
                    for (Component tc : p.getComponents()) {
                        if (tc instanceof JLabel) {
                            JLabel lbl = (JLabel) tc;
                            if (lbl.getFont().getSize() == 11) { // title label
                                lbl.setForeground(UIPalette.TEXT_SECONDARY);
                            } else { // sub label
                                lbl.setForeground(UIPalette.ON_BACKGROUND);
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateThemeColors() {
        setBackground(UIPalette.BACKGROUND);
        if (title != null) title.setForeground(UIPalette.ON_BACKGROUND);
        if (shortcutTitle != null) shortcutTitle.setForeground(UIPalette.ON_BACKGROUND);

        if (warningBannerText != null) {
            warningBannerText.setForeground(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0xFECACA) : UIPalette.ERROR);
        }

        // Atualiza KPI cards
        if (kpiCards.size() == 4) {
            updateCardStyle(kpiCards.get(0), UIPalette.ON_BACKGROUND);
            updateCardStyle(kpiCards.get(1), UIPalette.SUCCESS);
            updateCardStyle(kpiCards.get(2), UIPalette.ERROR);
            updateCardStyle(kpiCards.get(3), UIPalette.AMBER_DARK);
        }

        // Atualiza Shortcuts
        for (JPanel card : shortcutCards) {
            updateShortcutStyle(card);
        }

        // Atualiza Tabela de Alertas e Container
        if (cardContainer != null) {
            cardContainer.setBackground(UIPalette.SURFACE);
            cardContainer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIPalette.BORDER),
                    new EmptyBorder(16, 16, 16, 16)));
        }
        if (alertTitle != null) alertTitle.setForeground(UIPalette.ON_BACKGROUND);
        if (alertTable != null) {
            alertTable.setBackground(UIPalette.SURFACE);
            alertTable.setForeground(UIPalette.ON_BACKGROUND);
            alertTable.getTableHeader().setBackground(UIPalette.SURFACE);
            alertTable.getTableHeader().setForeground(UIPalette.ON_BACKGROUND);
            alertTable.setGridColor(UIPalette.BORDER);
        }
        if (alertScroll != null) {
            alertScroll.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
            alertScroll.getViewport().setBackground(UIPalette.SURFACE);
        }
        if (emptyState != null) {
            emptyState.setBackground(UIPalette.SURFACE);
            emptyLabel.setForeground(UIPalette.TEXT_SECONDARY);
        }

        // Atualiza Gráfico
        if (chart != null && chartPanel != null) {
            org.knowm.xchart.style.CategoryStyler styler = chart.getStyler();
            styler.setChartBackgroundColor(UIPalette.SURFACE);
            styler.setPlotBackgroundColor(UIPalette.SURFACE);
            styler.setChartFontColor(UIPalette.ON_BACKGROUND);
            styler.setChartTitleBoxVisible(false);
            styler.setLegendBackgroundColor(UIPalette.SURFACE);
            styler.setLegendBorderColor(UIPalette.BORDER);
            styler.setSeriesColors(new Color[]{UIPalette.AMBER, com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x475569) : new Color(0xCBD5E1)});
            styler.setAxisTickLabelsColor(UIPalette.ON_BACKGROUND);
            styler.setPlotGridLinesColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x334155) : new Color(0xE2E8F0));
            chartPanel.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
            chartPanel.repaint();
        }

        repaint();
    }
}
