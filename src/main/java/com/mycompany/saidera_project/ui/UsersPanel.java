package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.User;
import com.mycompany.saidera_project.security.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsersPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JLabel totalUsersValueLabel;
    private JLabel sessionValueLabel;
    private JLabel adminsValueLabel;

    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Usuários");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(32f));
        title.setForeground(UIPalette.ON_BACKGROUND);
        titlePanel.add(title);

        JLabel sub = new JLabel("Gerencie os acessos e perfis da sua equipe.");
        sub.setFont(UIPalette.FONT_BODY);
        sub.setForeground(UIPalette.TEXT_SECONDARY);
        titlePanel.add(sub);

        header.add(titlePanel, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Novo Usuário");
        addBtn.setBackground(UIPalette.AMBER);
        addBtn.setFont(UIPalette.FONT_LABEL);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

        addBtn.setEnabled(isAdmin);
        if (!isAdmin) {
            addBtn.setToolTipText("Apenas administradores podem cadastrar novos usuários.");
        }

        addBtn.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new UserForm((Frame) owner, this::refreshTable).setVisible(true);
        });
        header.add(addBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Metrics and Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Metrics Row
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createMetricCard("USUÁRIOS CADASTRADOS", "00", "no sistema", "👥", new Color(0xEFF6FF)));
        kpiPanel.add(createMetricCard("SESSÃO ATIVA", "00", "neste terminal", "🔑", new Color(0xECFDF5)));
        kpiPanel.add(createMetricCard("PERFIS ADMIN", "00", "com permissão", "👑", new Color(0xFFFBEB)));
        centerPanel.add(kpiPanel, BorderLayout.NORTH);

        updateMetrics();

        // Table Model Setup
        String[] columns = { "ID", "FUNCIONÁRIO", "EMAIL", "CARGO/PERFIL", "DATA DE CADASTRO", "AÇÕES" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        refreshTable();

        table = new JTable(model);
        table.setRowHeight(50);

        // Setup Action Column
        TableActionCell actionCell = new TableActionCell(
                e -> JOptionPane.showMessageDialog(this, "Edição indisponível na versão demo."),
                e -> {
                    if (!isAdmin) {
                        JOptionPane.showMessageDialog(this, "Você não tem permissão para excluir usuários.",
                                "Acesso Restrito", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int row = Integer.parseInt(e.getActionCommand());
                    if (row < 0 || row >= model.getRowCount())
                        return;
                    table.getSelectionModel().setSelectionInterval(row, row);
                    String id = (String) model.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(this, "Excluir este usuário?", "Confirmar",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DataRepository.getInstance().deleteUser(id);
                        refreshTable();
                    }
                });
        table.getColumnModel().getColumn(5).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(5).setCellEditor(actionCell);

        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);

        // Custom Renderer for column 1 (Initials + Name Avatar)
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            private final JLabel nameLabel = new JLabel();
            private final JPanel avatarCircle = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int hash = nameText.hashCode();
                    Color[] presets = {
                        new Color(0x3B82F6), new Color(0x10B981), new Color(0xF59E0B), 
                        new Color(0xEF4444), new Color(0x8B5CF6), new Color(0xEC4899),
                        new Color(0x14B8A6), new Color(0x6366F1)
                    };
                    Color color = presets[Math.abs(hash) % presets.length];
                    g2.setColor(color);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            private final JLabel initialsLabel = new JLabel();
            private String nameText = "";
            {
                panel.setOpaque(false);
                avatarCircle.setPreferredSize(new Dimension(32, 32));
                avatarCircle.setOpaque(false);
                initialsLabel.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
                initialsLabel.setForeground(Color.WHITE);
                avatarCircle.add(initialsLabel);
                panel.add(avatarCircle);
                
                nameLabel.setFont(UIPalette.FONT_BODY);
                panel.add(nameLabel);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (value != null) {
                    nameText = value.toString();
                    nameLabel.setText(nameText);
                    
                    String initials = "";
                    String[] parts = nameText.trim().split("\\s+");
                    if (parts.length > 0 && !parts[0].isEmpty()) {
                        initials += parts[0].substring(0, 1).toUpperCase();
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            initials += parts[1].substring(0, 1).toUpperCase();
                        }
                    }
                    initialsLabel.setText(initials);
                    
                    if (isSelected) {
                        panel.setBackground(table.getSelectionBackground());
                        nameLabel.setForeground(table.getSelectionForeground());
                    } else {
                        panel.setBackground(table.getBackground());
                        nameLabel.setForeground(UIPalette.ON_BACKGROUND);
                    }
                    return panel;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Custom Renderer for column 3 (Semantic Role Badge)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private final JPanel badge = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(border);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
            };
            private final JLabel roleLabel = new JLabel();
            private Color bg = Color.WHITE;
            private Color border = Color.LIGHT_GRAY;
            {
                badge.setOpaque(false);
                badge.setLayout(new GridBagLayout());
                badge.setPreferredSize(new Dimension(80, 22));
                roleLabel.setFont(UIPalette.FONT_LABEL.deriveFont(9f));
                badge.add(roleLabel);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 14));
                wrapper.setOpaque(true);
                if (value != null) {
                    String role = value.toString();
                    roleLabel.setText(role.toUpperCase());
                    
                    if (role.equalsIgnoreCase("Admin")) {
                        bg = new Color(0xFEF3C7);
                        border = new Color(0xF59E0B);
                        roleLabel.setForeground(new Color(0x78350F));
                    } else if (role.equalsIgnoreCase("Gerente")) {
                        bg = new Color(0xDBEAFE);
                        border = new Color(0x3B82F6);
                        roleLabel.setForeground(new Color(0x1E3A8A));
                    } else if (role.equalsIgnoreCase("Caixa")) {
                        bg = new Color(0xF3E8FF);
                        border = new Color(0x8B5CF6);
                        roleLabel.setForeground(new Color(0x581C87));
                    } else { // Garçom or WAITER
                        bg = new Color(0xF3F4F6);
                        border = new Color(0x9CA3AF);
                        roleLabel.setForeground(new Color(0x374151));
                    }
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIPalette.BORDER));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Section: Role summary & current session
        JPanel footer = new JPanel(new GridLayout(1, 2, 30, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Role summary card
        JPanel roleCard = new JPanel(new BorderLayout());
        roleCard.setBackground(UIPalette.SLATE);
        roleCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel roleTitle = new JLabel("PERFIS CADASTRADOS");
        roleTitle.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        roleTitle.setForeground(Color.GRAY);
        roleCard.add(roleTitle, BorderLayout.NORTH);

        Map<String, Integer> roleCounts = new LinkedHashMap<>();
        for (User user : DataRepository.getInstance().getUsers()) {
            String role = user.getRole() != null ? user.getRole() : "Sem perfil";
            roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
        }

        StringBuilder roleText = new StringBuilder("<html>");
        for (Map.Entry<String, Integer> entry : roleCounts.entrySet()) {
            roleText.append("• ").append(entry.getKey()).append(": ").append(String.format("%02d", entry.getValue()))
                .append("<br>");
        }
        roleText.append("</html>");

        JLabel roleContent = new JLabel(roleText.toString());
        roleContent.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        roleContent.setForeground(Color.WHITE);
        roleCard.add(roleContent, BorderLayout.CENTER);

        // Current session card
        JPanel sessionCard = new JPanel(new BorderLayout());
        sessionCard.setBackground(Color.WHITE);
        sessionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIPalette.BORDER),
            new EmptyBorder(20, 20, 20, 20)));
        JLabel sessionTitle = new JLabel("SESSÃO ATUAL");
        sessionTitle.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        sessionTitle.setForeground(UIPalette.INFO);
        sessionCard.add(sessionTitle, BorderLayout.NORTH);

        User activeUser = SessionManager.getInstance().getCurrentUser();
        String sessionText = (activeUser != null)
            ? "<html>• Usuário: " + activeUser.getName() + "<br>• Perfil: " + activeUser.getRole() + "</html>"
            : "<html>• Nenhuma sessão ativa</html>";
        JLabel sessionContent = new JLabel(sessionText);
        sessionContent.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        sessionCard.add(sessionContent, BorderLayout.CENTER);

        footer.add(roleCard);
        footer.add(sessionCard);

        add(footer, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        model.setRowCount(0);
        List<User> userList = DataRepository.getInstance().getUsers();
        for (User u : userList) {
            model.addRow(
                    new Object[] { u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getRegistrationDate(), "" });
        }

        updateMetrics();
    }

    private JPanel createMetricCard(String title, String value, String sub, String iconText, Color iconBg) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIPalette.BORDER),
                new EmptyBorder(15, 20, 15, 20)));

        // Circle icon
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
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

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        t.setForeground(Color.GRAY);
        textContainer.add(t);

        JLabel v = new JLabel(value);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(26f));
        v.setForeground(UIPalette.ON_BACKGROUND);
        textContainer.add(v);

        if ("USUÁRIOS CADASTRADOS".equals(title)) {
            totalUsersValueLabel = v;
        } else if ("SESSÃO ATIVA".equals(title)) {
            sessionValueLabel = v;
        } else if ("PERFIS ADMIN".equals(title)) {
            adminsValueLabel = v;
        }

        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 10f));
        s.setForeground(Color.GRAY);
        textContainer.add(s);

        card.add(textContainer, BorderLayout.CENTER);
        return card;
    }

    private void updateMetrics() {
        List<User> userList = DataRepository.getInstance().getUsers();
        int total = userList.size();
        int admins = (int) userList.stream().filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("Admin"))
                .count();
        int session = SessionManager.getInstance().isLoggedIn() ? 1 : 0;

        if (totalUsersValueLabel != null)
            totalUsersValueLabel.setText(String.format("%02d", total));
        if (adminsValueLabel != null)
            adminsValueLabel.setText(String.format("%02d", admins));
        if (sessionValueLabel != null)
            sessionValueLabel.setText(String.format("%02d", session));
    }
}
