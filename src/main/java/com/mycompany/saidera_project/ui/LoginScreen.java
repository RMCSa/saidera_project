package com.mycompany.saidera_project.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Saíderas Desktop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main content pane with Split Layout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // LEFT PANEL: Brand Info
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(UIPalette.SLATE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        JLabel logoLabel = new JLabel("Saíderas Desktop");
        logoLabel.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        logoLabel.setForeground(UIPalette.AMBER);
        gbc.gridy = 0;
        leftPanel.add(logoLabel, gbc);

        JLabel sloganLabel = new JLabel("<html>Gerencie seu estabelecimento<br>com inteligência.</html>");
        sloganLabel.setFont(UIPalette.FONT_DISPLAY.deriveFont(24f));
        sloganLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(40, 40, 10, 40);
        leftPanel.add(sloganLabel, gbc);

        JLabel subLabel = new JLabel("<html>Acesse o back-office para controlar estoques,<br>pedidos e relatórios em tempo real.</html>");
        subLabel.setFont(UIPalette.FONT_BODY);
        subLabel.setForeground(new Color(0xBBBBBB));
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 40, 20, 40);
        leftPanel.add(subLabel, gbc);

        // RIGHT PANEL: Login Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UIPalette.SURFACE);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.insets = new Insets(10, 50, 10, 50);
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.gridx = 0;

        JLabel welcomeLabel = new JLabel("Bem-vindo de volta");
        welcomeLabel.setFont(UIPalette.FONT_TITLE.deriveFont(28f));
        welcomeLabel.setForeground(UIPalette.ON_BACKGROUND);
        gbcR.gridy = 0;
        gbcR.insets = new Insets(0, 50, 5, 50);
        rightPanel.add(welcomeLabel, gbcR);

        JLabel hintLabel = new JLabel("Por favor, insira suas credenciais para entrar");
        hintLabel.setFont(UIPalette.FONT_BODY);
        hintLabel.setForeground(UIPalette.TEXT_SECONDARY);
        gbcR.gridy = 1;
        gbcR.insets = new Insets(0, 50, 30, 50);
        rightPanel.add(hintLabel, gbcR);

        // Fields
        JLabel userLabel = new JLabel("USUÁRIO OU E-MAIL");
        userLabel.setFont(UIPalette.FONT_LABEL);
        gbcR.gridy = 2;
        gbcR.insets = new Insets(10, 50, 5, 50);
        rightPanel.add(userLabel, gbcR);

        JTextField userField = new JTextField();
        userField.setPreferredSize(new Dimension(300, 45));
        gbcR.gridy = 3;
        rightPanel.add(userField, gbcR);

        JLabel passLabel = new JLabel("SENHA");
        passLabel.setFont(UIPalette.FONT_LABEL);
        gbcR.gridy = 4;
        gbcR.insets = new Insets(15, 50, 5, 50);
        rightPanel.add(passLabel, gbcR);

        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(300, 45));
        gbcR.gridy = 5;
        rightPanel.add(passField, gbcR);

        // Remember Me & Forgot Password
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        JCheckBox rememberMe = new JCheckBox("Lembrar-me");
        rememberMe.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        optionsPanel.add(rememberMe, BorderLayout.WEST);
        
        JLabel forgotPass = new JLabel("<html><u>Esqueceu a senha?</u></html>");
        forgotPass.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(forgotPass, BorderLayout.EAST);
        
        gbcR.gridy = 6;
        gbcR.insets = new Insets(10, 50, 10, 50);
        rightPanel.add(optionsPanel, gbcR);

        // Login Button
        JButton loginBtn = new JButton("Entrar no Sistema");
        loginBtn.setBackground(UIPalette.AMBER);
        loginBtn.setForeground(UIPalette.ON_BACKGROUND);
        loginBtn.setFont(UIPalette.FONT_LABEL.deriveFont(16f));
        loginBtn.setPreferredSize(new Dimension(300, 50));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        gbcR.gridy = 7;
        gbcR.insets = new Insets(30, 50, 10, 50);
        rightPanel.add(loginBtn, gbcR);

        // SSL Indicator
        JPanel sslPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sslPanel.setOpaque(false);
        JLabel sslLabel = new JLabel("🔒 Ambiente Seguro SSL - v1.0.4");
        sslLabel.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        sslLabel.setForeground(Color.LIGHT_GRAY);
        sslPanel.add(sslLabel);
        gbcR.gridy = 8;
        rightPanel.add(sslPanel, gbcR);

        loginBtn.addActionListener((ActionEvent e) -> {
            // Simplified login logic: open MainFrame
            dispose();
            SwingUtilities.invokeLater(() -> {
                new MainFrame().setVisible(true);
            });
        });

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", UIPalette.ROUNDNESS);
        UIManager.put("Component.arc", UIPalette.ROUNDNESS);
        UIManager.put("TextComponent.arc", UIPalette.ROUNDNESS);
        
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
