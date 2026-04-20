package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.User;
import com.mycompany.saidera_project.security.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
public class UsersPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(UIPalette.BACKGROUND);
        setBorder(new EmptyBorder(40, 40, 40, 40));

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
        JPanel centerPanel = new JPanel(new BorderLayout(0, 30));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        // Metrics Row (Static for now)
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createMetricCard("TOTAL STAFF", "24", "membros"));
        kpiPanel.add(createMetricCard("ATIVOS AGORA", "08", "no turno"));
        kpiPanel.add(createMetricCard("NOVAS CONTRATAÇÕES", "+3", "este mês"));
        centerPanel.add(kpiPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "FUNCIONÁRIO", "EMAIL", "CARGO/PERFIL", "DATA DE CADASTRO", "AÇÕES"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        refreshTable();

        table = new JTable(model);
        table.setRowHeight(60);
        
        // Setup Action Column
        TableActionCell actionCell = new TableActionCell(
            e -> JOptionPane.showMessageDialog(this, "Editar funcionalidade em breve!"),
            e -> {
                if (!isAdmin) {
                    JOptionPane.showMessageDialog(this, "Você não tem permissão para excluir usuários.", "Acesso Restrito", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int row = table.getSelectedRow();
                String id = (String) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Excluir este usuário?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    DataRepository.getInstance().deleteUser(id);
                    refreshTable();
                }
            }
        );
        table.getColumnModel().getColumn(5).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(5).setCellEditor(actionCell);

        table.setFont(UIPalette.FONT_BODY);
        table.getTableHeader().setFont(UIPalette.FONT_LABEL);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Section: Activity Log & Security Tip
        JPanel footer = new JPanel(new GridLayout(1, 2, 30, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Activity Log Card (Dark)
        JPanel activityCard = new JPanel(new BorderLayout());
        activityCard.setBackground(UIPalette.SLATE);
        activityCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel actTitle = new JLabel("RELATÓRIO DE ATIVIDADE");
        actTitle.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        actTitle.setForeground(Color.GRAY);
        activityCard.add(actTitle, BorderLayout.NORTH);
        
        JLabel actContent = new JLabel("<html>• André Machado alterou estoque (Pilsen)<br>• Beatriz Silva iniciou turno no Caixa 01</html>");
        actContent.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        actContent.setForeground(Color.WHITE);
        activityCard.add(actContent, BorderLayout.CENTER);
        
        // Security Tip Card
        JPanel tipCard = new JPanel(new BorderLayout());
        tipCard.setBackground(new Color(0xE3F2FD));
        tipCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBBDEFB)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel tipTitle = new JLabel("DICA DE SEGURANÇA");
        tipTitle.setFont(UIPalette.FONT_LABEL.deriveFont(10f));
        tipTitle.setForeground(new Color(0x1976D2));
        tipCard.add(tipTitle, BorderLayout.NORTH);
        
        JLabel tipContent = new JLabel("Mantenha o acesso de Garçom restrito para evitar edições acidentais no cardápio.");
        tipContent.setFont(UIPalette.FONT_BODY.deriveFont(12f));
        tipCard.add(tipContent, BorderLayout.CENTER);
        
        footer.add(activityCard);
        footer.add(tipCard);
        
        add(footer, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        model.setRowCount(0);
        List<User> userList = DataRepository.getInstance().getUsers();
        for (User u : userList) {
            model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getRegistrationDate()});
        }
    }

    private JPanel createMetricCard(String title, String value, String sub) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(title.contains("NOVAS") ? new Color(0xFFF0BD) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel t = new JLabel(title);
        t.setFont(UIPalette.FONT_LABEL);
        t.setForeground(new Color(0x666666));
        gbc.gridy = 0;
        card.add(t, gbc);

        JPanel valPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valPanel.setOpaque(false);
        JLabel v = new JLabel(value);
        v.setFont(UIPalette.FONT_TITLE.deriveFont(28f));
        valPanel.add(v);
        JLabel s = new JLabel(sub);
        s.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN));
        s.setForeground(Color.GRAY);
        valPanel.add(s);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(valPanel, gbc);

        return card;
    }
}
