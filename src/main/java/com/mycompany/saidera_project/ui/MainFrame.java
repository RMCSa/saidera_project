package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private JPanel activeSidebarItem = null;

    public MainFrame() {
        setTitle("Saíderas Desktop - Gestão de Choperia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setBackground(UIPalette.SLATE);
        
        JPanel navContainer = new JPanel();
        navContainer.setOpaque(false);
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));

        // Sidebar Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        header.setOpaque(false);
        JLabel logoBrand = new JLabel("Saíderas 🍺");
        logoBrand.setFont(UIPalette.FONT_TITLE.deriveFont(24f));
        logoBrand.setForeground(UIPalette.AMBER);
        header.add(logoBrand);
        navContainer.add(header);
        navContainer.add(Box.createVerticalStrut(20));

        // Navigation Items
        addNavItem(navContainer, "📊  Dashboard", "Dashboard");
        addNavItem(navContainer, "🍴  Cardápio", "Menu");
        addNavItem(navContainer, "📦  Estoque", "Inventory");
        addNavItem(navContainer, "👥  Usuários", "Users");

        sidebar.add(navContainer, BorderLayout.NORTH);

        // Sidebar Footer (Capacity Bar)
        JPanel sidebarFooter = new JPanel(new BorderLayout(0, 10));
        sidebarFooter.setOpaque(false);
        sidebarFooter.setBorder(new EmptyBorder(0, 20, 40, 20));
        
        JLabel capLabel = new JLabel("CAPACIDADE TOTAL");
        capLabel.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        capLabel.setForeground(Color.GRAY);
        sidebarFooter.add(capLabel, BorderLayout.NORTH);
        
        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(78);
        progress.setForeground(UIPalette.AMBER);
        progress.setStringPainted(false);
        progress.setPreferredSize(new Dimension(0, 8));
        sidebarFooter.add(progress, BorderLayout.CENTER);
        
        JLabel capSub = new JLabel("Barris de Chopp: 78% ocupados");
        capSub.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        capSub.setForeground(Color.LIGHT_GRAY);
        sidebarFooter.add(capSub, BorderLayout.SOUTH);
        
        sidebar.add(sidebarFooter, BorderLayout.SOUTH);

        mainPanel.add(sidebar, BorderLayout.WEST);

        // RIGHT CONTAINER (Header + Content)
        JPanel rightContainer = new JPanel(new BorderLayout());
        
        SearchHeader topHeader = new SearchHeader("Buscar no sistema (produtos, usuários, estoque)...");
        rightContainer.add(topHeader, BorderLayout.NORTH);

        contentArea.setBackground(UIPalette.BACKGROUND);
        contentArea.add(new DashboardPanel(), "Dashboard");
        contentArea.add(new MenuPanel(), "Menu");
        contentArea.add(new InventoryPanel(), "Inventory");
        contentArea.add(new UsersPanel(), "Users");

        rightContainer.add(contentArea, BorderLayout.CENTER);
        mainPanel.add(rightContainer, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private void addNavItem(JPanel container, String text, String cardName) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(240, 50));
        
        JLabel label = new JLabel(text);
        label.setFont(UIPalette.FONT_BODY.deriveFont(Font.BOLD));
        label.setForeground(Color.LIGHT_GRAY);
        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (activeSidebarItem != null) {
                    activeSidebarItem.setOpaque(false);
                    ((JLabel)activeSidebarItem.getComponent(0)).setForeground(Color.LIGHT_GRAY);
                }
                item.setOpaque(true);
                item.setBackground(new Color(0, 0, 0, 40));
                label.setForeground(UIPalette.AMBER);
                activeSidebarItem = item;
                cardLayout.show(contentArea, cardName);
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeSidebarItem != item) {
                    item.setOpaque(true);
                    item.setBackground(new Color(255, 255, 255, 20));
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeSidebarItem != item) {
                    item.setOpaque(false);
                    repaint();
                }
            }
        });

        // Default active item
        if (text.contains("Dashboard")) {
            item.setOpaque(true);
            item.setBackground(new Color(0, 0, 0, 40));
            label.setForeground(UIPalette.AMBER);
            activeSidebarItem = item;
        }

        container.add(item);
    }
}
