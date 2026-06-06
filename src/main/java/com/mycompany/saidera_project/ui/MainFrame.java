package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.mycompany.saidera_project.data.DataRepository;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private JPanel activeSidebarItem = null;
    private JPanel navContainer;
    private SearchHeader topHeader;

    public void showPanel(String cardName) {
        cardLayout.show(contentArea, cardName);
        highlightNavItem(cardName);
    }

    public void highlightNavItem(String cardName) {
        for (Component c : navContainer.getComponents()) {
            if (c instanceof JPanel) {
                JPanel item = (JPanel) c;
                if (cardName.equals(item.getClientProperty("cardName"))) {
                    if (activeSidebarItem != null) {
                        activeSidebarItem.setOpaque(false);
                        for (Component comp : activeSidebarItem.getComponents()) {
                            if (comp instanceof JLabel && !((JLabel) comp).getForeground().equals(Color.WHITE)) {
                                comp.setForeground(Color.LIGHT_GRAY);
                            }
                        }
                    }
                    item.setOpaque(true);
                    item.setBackground(UIPalette.SLATE_LIGHT);
                    for (Component comp : item.getComponents()) {
                        if (comp instanceof JLabel && !((JLabel) comp).getForeground().equals(Color.WHITE)) {
                            comp.setForeground(UIPalette.AMBER);
                        }
                    }
                    activeSidebarItem = item;
                    repaint();
                    break;
                }
            }
        }
    }

    public MainFrame() {
        setTitle("Saidera Desktop - Gestão de Choperia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setBackground(UIPalette.SLATE);

        navContainer = new JPanel();
        navContainer.setOpaque(false);
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));

        // Sidebar Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 25));
        header.setOpaque(false);

        // Brand logo icon square box
        JPanel logoBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIPalette.AMBER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        logoBox.setPreferredSize(new Dimension(36, 36));
        logoBox.setOpaque(false);
        JLabel logoEmoji = new JLabel("🍺");
        logoEmoji.setFont(logoEmoji.getFont().deriveFont(18f));
        logoBox.add(logoEmoji);

        JLabel logoBrand = new JLabel("Saidera");
        logoBrand.setFont(UIPalette.FONT_TITLE.deriveFont(22f));
        logoBrand.setForeground(Color.WHITE);

        header.add(logoBox);
        header.add(logoBrand);
        navContainer.add(header);
        navContainer.add(Box.createVerticalStrut(15));

        // Navigation Items divided by Sections
        addSectionLabel(navContainer, "Principal");
        addNavItem(navContainer, "📊  Dashboard", "Dashboard");
        addNavItem(navContainer, "🍴  Cardápio", "Menu");
        addNavItem(navContainer, "📦  Estoque", "Inventory");

        navContainer.add(Box.createVerticalStrut(20));
        addSectionLabel(navContainer, "Administração");
        addNavItem(navContainer, "👥  Usuários", "Users");

        sidebar.add(navContainer, BorderLayout.NORTH);

        // Sidebar Footer (Capacity Bar)
        JPanel sidebarFooter = new JPanel(new BorderLayout(0, 12));
        sidebarFooter.setOpaque(false);
        sidebarFooter.setBorder(new EmptyBorder(15, 20, 40, 20));

        JLabel capLabel = new JLabel("CAPACIDADE TOTAL");
        capLabel.setFont(UIPalette.FONT_LABEL.deriveFont(11f));
        capLabel.setForeground(new Color(0x94A3B8));
        sidebarFooter.add(capLabel, BorderLayout.NORTH);

        JProgressBar progress = new JProgressBar(0, 100);
        int totalItems = DataRepository.getInstance().getInventory().size();
        int lowStockItems = DataRepository.getInstance().getLowStockCount();
        int stablePercent = totalItems == 0 ? 0 : Math.max(0, ((totalItems - lowStockItems) * 100) / totalItems);
        progress.setValue(stablePercent);
        progress.setForeground(UIPalette.AMBER);
        progress.setStringPainted(false);
        progress.setPreferredSize(new Dimension(0, 10));
        progress.putClientProperty("FlatLaf.style", "foreground: #FFBF00; background: #2C4259;");
        sidebarFooter.add(progress, BorderLayout.CENTER);

        JLabel capSub = new JLabel(String.format("Estoque estável: %02d%%", stablePercent));
        capSub.setFont(UIPalette.FONT_LABEL.deriveFont(11f));
        capSub.setForeground(Color.LIGHT_GRAY);
        sidebarFooter.add(capSub, BorderLayout.SOUTH);

        sidebar.add(sidebarFooter, BorderLayout.SOUTH);

        mainPanel.add(sidebar, BorderLayout.WEST);

        // RIGHT CONTAINER (Header + Content)
        JPanel rightContainer = new JPanel(new BorderLayout());

        topHeader = new SearchHeader();
        rightContainer.add(topHeader, BorderLayout.NORTH);

        contentArea.setBackground(UIPalette.BACKGROUND);
        contentArea.add(new DashboardPanel(), "Dashboard");
        contentArea.add(new MenuPanel(), "Menu");
        contentArea.add(new InventoryPanel(), "Inventory");
        contentArea.add(new UsersPanel(), "Users");

        // Ensure initial view matches the highlighted sidebar item
        cardLayout.show(contentArea, "Dashboard");

        rightContainer.add(contentArea, BorderLayout.CENTER);
        mainPanel.add(rightContainer, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void addSectionLabel(JPanel container, String text) {
        JPanel sectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        sectionPanel.setOpaque(false);
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        label.setForeground(new Color(0x64748B)); // Slate subtext color
        sectionPanel.add(label);
        container.add(sectionPanel);
    }

    private void addNavItem(JPanel container, String text, String cardName) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(240, 42));
        item.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        item.putClientProperty("cardName", cardName);

        JLabel label = new JLabel(text);
        label.setFont(UIPalette.FONT_BODY.deriveFont(Font.BOLD));
        label.setForeground(Color.LIGHT_GRAY);
        item.add(label, BorderLayout.WEST);

        // Add badge for inventory if it has low stock
        if (cardName.equals("Inventory")) {
            int lowStock = DataRepository.getInstance().getLowStockCount();
            if (lowStock > 0) {
                JPanel badge = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(UIPalette.ERROR);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.dispose();
                    }
                };
                badge.setOpaque(false);
                badge.setLayout(new GridBagLayout());
                badge.setPreferredSize(new Dimension(22, 18));
                JLabel badgeLabel = new JLabel(String.valueOf(lowStock));
                badgeLabel.setFont(UIPalette.FONT_LABEL.deriveFont(9f));
                badgeLabel.setForeground(Color.WHITE);
                badge.add(badgeLabel);
                item.add(badge, BorderLayout.EAST);
            }
        }

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (activeSidebarItem != null) {
                    activeSidebarItem.setOpaque(false);
                    for (Component comp : activeSidebarItem.getComponents()) {
                        if (comp instanceof JLabel && !((JLabel) comp).getForeground().equals(Color.WHITE)) {
                            comp.setForeground(Color.LIGHT_GRAY);
                        }
                    }
                }
                item.setOpaque(true);
                item.setBackground(UIPalette.SLATE_LIGHT);
                label.setForeground(UIPalette.AMBER);
                activeSidebarItem = item;
                cardLayout.show(contentArea, cardName);
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeSidebarItem != item) {
                    item.setOpaque(true);
                    item.setBackground(new Color(255, 255, 255, 15));
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
            item.setBackground(UIPalette.SLATE_LIGHT);
            label.setForeground(UIPalette.AMBER);
            activeSidebarItem = item;
        }

        container.add(item);
    }

    public void updateThemeColors() {
        if (topHeader != null) {
            topHeader.updateThemeColors();
        }
        if (contentArea != null) {
            contentArea.setBackground(UIPalette.BACKGROUND);
            for (Component c : contentArea.getComponents()) {
                if (c instanceof DashboardPanel) {
                    ((DashboardPanel) c).updateThemeColors();
                } else if (c instanceof MenuPanel) {
                    ((MenuPanel) c).updateThemeColors();
                } else if (c instanceof UsersPanel) {
                    ((UsersPanel) c).updateThemeColors();
                } else if (c instanceof InventoryPanel) {
                    ((InventoryPanel) c).updateThemeColors();
                }
            }
        }
        repaint();
    }
}
