package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable header with search bar and profile info.
 */
public class SearchHeader extends JPanel {

    public SearchHeader(String searchPlaceholder) {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 40, 10, 40));
        setPreferredSize(new Dimension(0, 70));

        // Search Bar Area
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchBar.setOpaque(false);
        
        JTextField searchField = new JTextField(searchPlaceholder);
        searchField.setPreferredSize(new Dimension(400, 35));
        searchField.setForeground(Color.GRAY);
        searchBar.add(new JLabel("🔍"));
        searchBar.add(searchField);
        
        add(searchBar, BorderLayout.WEST);

        // Right side: Notifications, Settings, Profile
        JPanel rightArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightArea.setOpaque(false);
        
        rightArea.add(new JLabel("🔔"));
        rightArea.add(new JLabel("⚙️"));
        
        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);
        JLabel name = new JLabel("Gerente Geral");
        name.setFont(UIPalette.FONT_LABEL);
        profile.add(name);
        JLabel avatar = new JLabel("👤");
        profile.add(avatar);
        
        rightArea.add(profile);
        add(rightArea, BorderLayout.EAST);
        
        // Bottom border line
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            new EmptyBorder(10, 40, 10, 40)
        ));
    }
}
