package com.mycompany.saidera_project.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.User;
import com.mycompany.saidera_project.security.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Saidera Desktop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main content pane with Split Layout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // LEFT PANEL: Brand Info with beautiful custom gradient
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Radial/linear-like gradient from Slate to slate-light
                GradientPaint gp = new GradientPaint(0, 0, UIPalette.SLATE, 0, getHeight(), new Color(0x0F1923));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        JLabel logoLabel = new JLabel("Saidera Desktop");
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

        JLabel subLabel = new JLabel(
                "<html>Acesse o back-office para controlar estoques,<br>pedidos e relatórios em tempo real.</html>");
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
        JLabel userLabel = new JLabel("E-MAIL");
        userLabel.setFont(UIPalette.FONT_LABEL);
        gbcR.gridy = 2;
        gbcR.insets = new Insets(10, 50, 5, 50);
        rightPanel.add(userLabel, gbcR);

        JTextField userField = new JTextField();
        userField.setPreferredSize(new Dimension(300, 45));
        userField.putClientProperty("JTextField.placeholderText", "exemplo@choperia.com.br");
        userField.putClientProperty("JTextField.showClearButton", true);
        gbcR.gridy = 3;
        rightPanel.add(userField, gbcR);

        JLabel passLabel = new JLabel("SENHA");
        passLabel.setFont(UIPalette.FONT_LABEL);
        gbcR.gridy = 4;
        gbcR.insets = new Insets(15, 50, 5, 50);
        rightPanel.add(passLabel, gbcR);

        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(300, 45));
        passField.putClientProperty("JTextField.placeholderText", "Sua senha");
        passField.putClientProperty("JTextField.showRevealButton", true);
        gbcR.gridy = 5;
        rightPanel.add(passField, gbcR);

        // Remember Me & Forgot Password
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        JCheckBox rememberMe = new JCheckBox("Lembrar-me");
        rememberMe.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        rememberMe.setToolTipText("Modo demonstração: opção sem efeito.");
        optionsPanel.add(rememberMe, BorderLayout.WEST);

        JLabel forgotPass = new JLabel("<html><u>Esqueceu a senha?</u></html>");
        forgotPass.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPass.setToolTipText("Modo demonstração: recuperação indisponível.");
        forgotPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginScreen.this,
                        "Recuperação de senha indisponível na versão demo.",
                        "Modo demonstração",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        optionsPanel.add(forgotPass, BorderLayout.EAST);

        gbcR.gridy = 6;
        gbcR.insets = new Insets(10, 50, 10, 50);
        rightPanel.add(optionsPanel, gbcR);

        // Inline Error Label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(UIPalette.FONT_LABEL.deriveFont(13f));
        errorLabel.setForeground(UIPalette.ERROR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbcR.gridy = 7;
        gbcR.insets = new Insets(5, 50, 5, 50);
        rightPanel.add(errorLabel, gbcR);

        // Login Button
        JButton loginBtn = new JButton("Entrar no Sistema");
        loginBtn.setBackground(UIPalette.AMBER);
        loginBtn.setForeground(UIPalette.ON_AMBER);
        loginBtn.setFont(UIPalette.FONT_LABEL.deriveFont(16f));
        loginBtn.setPreferredSize(new Dimension(300, 50));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        gbcR.gridy = 8;
        gbcR.insets = new Insets(15, 50, 10, 50);
        rightPanel.add(loginBtn, gbcR);

        // // Demo indicator
        // JPanel sslPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // sslPanel.setOpaque(false);
        // JLabel sslLabel = new JLabel("Modo demonstração com dados mockados");
        // sslLabel.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        // sslLabel.setForeground(Color.LIGHT_GRAY);
        // sslPanel.add(sslLabel);
        // gbcR.gridy = 9;
        // rightPanel.add(sslPanel, gbcR);

        loginBtn.addActionListener((ActionEvent e) -> {
            String email = userField.getText();
            String password = new String(passField.getPassword());

            // Clear previous outlines and error
            errorLabel.setText(" ");
            userField.putClientProperty("JComponent.outline", null);
            passField.putClientProperty("JComponent.outline", null);
            rightPanel.revalidate();
            rightPanel.repaint();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Por favor, preencha as credenciais.");
                if (email.isEmpty()) {
                    userField.putClientProperty("JComponent.outline", "error");
                    userField.requestFocusInWindow();
                } else {
                    passField.putClientProperty("JComponent.outline", "error");
                    passField.requestFocusInWindow();
                }
                rightPanel.revalidate();
                rightPanel.repaint();
                return;
            }

            // Disable controls to show loading state
            loginBtn.setEnabled(false);
            loginBtn.setText("Autenticando...");
            userField.setEnabled(false);
            passField.setEnabled(false);

            // Simulating authenticating in SwingWorker for smooth feedback
            SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
                @Override
                protected User doInBackground() throws Exception {
                    Thread.sleep(850); // smooth indicator delay
                    return DataRepository.getInstance().authenticate(email, password);
                }

                @Override
                protected void done() {
                    try {
                        User authenticatedUser = get();
                        if (authenticatedUser != null) {
                            SessionManager.getInstance().login(authenticatedUser);
                            dispose();
                            SwingUtilities.invokeLater(() -> {
                                new MainFrame().setVisible(true);
                            });
                        } else {
                            // Re-enable input
                            loginBtn.setEnabled(true);
                            loginBtn.setText("Entrar no Sistema");
                            userField.setEnabled(true);
                            passField.setEnabled(true);

                            errorLabel.setText("E-mail ou senha inválidos.");
                            userField.putClientProperty("JComponent.outline", "error");
                            passField.putClientProperty("JComponent.outline", "error");
                            rightPanel.revalidate();
                            rightPanel.repaint();
                            passField.requestFocusInWindow();
                            passField.selectAll();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Entrar no Sistema");
                        userField.setEnabled(true);
                        passField.setEnabled(true);
                        errorLabel.setText("Erro de conexão ao banco de dados.");
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                }
            };
            worker.execute();
        });

        getRootPane().setDefaultButton(loginBtn);

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
