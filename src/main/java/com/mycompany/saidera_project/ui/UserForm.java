package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserForm extends BaseDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;
    private JComboBox<String> roleCombo;
    private Runnable onSaveCallback;
    /** Usuário sendo editado; null em modo criação. */
    private User editingUser;

    /** Construtor para criação de novo usuário. */
    public UserForm(Frame owner, Runnable onSaveCallback) {
        this(owner, onSaveCallback, null);
    }

    /** Construtor para edição de usuário existente. */
    public UserForm(Frame owner, Runnable onSaveCallback, User existing) {
        super(owner, existing == null ? "Novo Usuário" : "Editar Usuário", 500, 600);
        this.onSaveCallback = onSaveCallback;
        this.editingUser = existing;

        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(0, 40));
        if (existing != null) nameField.setText(existing.getName());
        addField("NOME COMPLETO", nameField, gbc, 0);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(0, 40));
        if (existing != null) emailField.setText(existing.getEmail());
        addField("E-MAIL PROFISSIONAL", emailField, gbc, 1);

        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(0, 40));
        passField.setToolTipText(existing == null
            ? "Se vazio, será usada a senha temporária padrão: muda123 (demo)."
            : "Deixe em branco para manter a senha atual.");
        addField(existing == null ? "SENHA TEMPORÁRIA" : "NOVA SENHA (opcional)", passField, gbc, 2);

        roleCombo = new JComboBox<>(new String[] { "Admin", "Gerente", "Caixa", "Garçom" });
        roleCombo.setPreferredSize(new Dimension(0, 40));
        if (existing != null) roleCombo.setSelectedItem(existing.getRole());
        addField("CARGO / PERFIL", roleCombo, gbc, 3);

        // Buttons
        JButton cancelBtn = createSecondaryButton("Cancelar");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createPrimaryButton(editingUser == null ? "Salvar Usuário" : "Atualizar Usuário");
        saveBtn.addActionListener(this::saveUser);

        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);
    }

    private void saveUser(ActionEvent e) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha nome e e-mail.");
            return;
        }

        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            JOptionPane.showMessageDialog(this, "Informe um e-mail válido.");
            return;
        }

        if (editingUser != null) {
            // Modo edição: senha vazia = mantém a atual (updateUser trata isso)
            User updated = new User(editingUser.getId(), name, email, role,
                    editingUser.getRegistrationDate(), password);
            DataRepository.getInstance().updateUser(updated);
        } else {
            // Modo criação
            if (password.isEmpty()) {
                password = "muda123";
            }
            String id = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 28);
            String dateStr = new SimpleDateFormat("dd MMM, yyyy").format(new Date());
            User u = new User(id, name, email, role, dateStr, password);
            DataRepository.getInstance().addUser(u);
        }

        if (onSaveCallback != null)
            onSaveCallback.run();
        dispose();
    }
}
