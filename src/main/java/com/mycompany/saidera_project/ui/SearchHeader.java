package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.models.User;
import com.mycompany.saidera_project.security.SessionManager;
import com.mycompany.saidera_project.ui.LoginScreen;
import com.mycompany.saidera_project.data.DataRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clean top header with profile info and actions.
 */
public class SearchHeader extends JPanel {

    public SearchHeader() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 60));

        // Right side: Notifications, Settings, Profile
        JPanel rightArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        rightArea.setOpaque(false);

        // rightArea.add(new JLabel("🔔"));
        // rightArea.add(new JLabel("⚙️"));

        User activeUser = SessionManager.getInstance().getCurrentUser();
        String userName = (activeUser != null) ? activeUser.getName() : "Não Identificado";
        String userRole = (activeUser != null) ? activeUser.getRole() : "Convidado";

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);
        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(UIPalette.FONT_LABEL);
        profile.add(nameLabel);

        JLabel roleLabel = new JLabel("(" + userRole + ")");
        roleLabel.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        roleLabel.setForeground(Color.GRAY);
        profile.add(roleLabel);

        JLabel avatar = new JLabel("👤");
        profile.add(avatar);

        rightArea.add(profile);

        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 12f));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            Window w = SwingUtilities.getWindowAncestor(SearchHeader.this);
            if (w != null)
                w.dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        });
        rightArea.add(logoutBtn);
        add(rightArea, BorderLayout.EAST);

        // Left side: Clickable Alert Pill if there are low stock alerts
        int lowStockCount = DataRepository.getInstance().getLowStockCount();
        if (lowStockCount > 0) {
            JPanel alertPill = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xFEE2E2)); // red-50
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(UIPalette.ERROR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            alertPill.setOpaque(false);
            alertPill.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 5));
            alertPill.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JLabel alertText = new JLabel("🚨 " + lowStockCount + " Alertas de Estoque Baixo");
            alertText.setFont(UIPalette.FONT_LABEL);
            alertText.setForeground(UIPalette.ERROR);
            alertPill.add(alertText);
            
            alertPill.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    Window w = SwingUtilities.getWindowAncestor(SearchHeader.this);
                    if (w instanceof MainFrame) {
                        ((MainFrame) w).showPanel("Inventory");
                    }
                }
            });
            
            JPanel leftArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 13));
            leftArea.setOpaque(false);
            leftArea.add(alertPill);
            add(leftArea, BorderLayout.WEST);
        }

        // Bottom border line using design system BORDER token
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIPalette.BORDER),
                new EmptyBorder(0, 40, 0, 40)));
    }
}
