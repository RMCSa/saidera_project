package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.models.User;
import com.mycompany.saidera_project.security.SessionManager;
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
        add(rightArea, BorderLayout.EAST);
        
        // Bottom border line
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            new EmptyBorder(0, 40, 0, 40)
        ));
    }
}
