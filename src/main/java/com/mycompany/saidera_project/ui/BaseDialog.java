package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable base dialog with premium styling matching "Amber Ledger" design.
 */
public class BaseDialog extends JDialog {
    protected JPanel contentPanel;
    protected JPanel footerPanel;

    public BaseDialog(Frame owner, String title, int width, int height) {
        super(owner, title, true);
        setSize(width, height);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        headerPanel.setBackground(UIPalette.SLATE);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIPalette.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(UIPalette.AMBER);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(UIPalette.SURFACE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 10, 30));
        add(contentPanel, BorderLayout.CENTER);

        // Footer Panel
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerPanel.setBackground(UIPalette.SURFACE);
        footerPanel.setBorder(new EmptyBorder(0, 0, 10, 10));
        add(footerPanel, BorderLayout.SOUTH);
    }

    protected void addField(String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel l = new JLabel(label);
        l.setFont(UIPalette.FONT_LABEL);
        contentPanel.add(l, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(field, gbc);
    }

    protected JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIPalette.AMBER);
        btn.setForeground(UIPalette.ON_BACKGROUND);
        btn.setFont(UIPalette.FONT_LABEL);
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    protected JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIPalette.FONT_LABEL);
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }
}
