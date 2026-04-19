package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProductForm extends BaseDialog {
    private JTextField nameField;
    private JTextField categoryField;
    private JSpinner priceSpinner;
    private JCheckBox activeBox;
    private Runnable onSaveCallback;

    public ProductForm(Frame owner, Runnable onSaveCallback) {
        super(owner, "Novo Produto", 500, 600);
        this.onSaveCallback = onSaveCallback;

        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(0, 40));
        addField("NOME DO PRODUTO", nameField, gbc, 0);

        categoryField = new JTextField();
        categoryField.setPreferredSize(new Dimension(0, 40));
        addField("CATEGORIA", categoryField, gbc, 1);

        priceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 10000.00, 0.50));
        priceSpinner.setPreferredSize(new Dimension(0, 40));
        addField("PREÇO (R$)", priceSpinner, gbc, 2);

        activeBox = new JCheckBox("Produto Ativo no Cardápio", true);
        activeBox.setFont(UIPalette.FONT_BODY);
        gbc.gridy = 6;
        contentPanel.add(activeBox, gbc);

        // Buttons
        JButton cancelBtn = createSecondaryButton("Cancelar");
        cancelBtn.addActionListener(e -> dispose());
        
        JButton saveBtn = createPrimaryButton("Salvar Produto");
        saveBtn.addActionListener(this::saveProduct);

        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);
    }

    private void saveProduct(ActionEvent e) {
        String name = nameField.getText();
        String category = categoryField.getText();
        double price = (Double) priceSpinner.getValue();
        boolean active = activeBox.isSelected();

        if (name.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
            return;
        }

        String id = String.format("%02d", DataRepository.getInstance().getProducts().size() + 1);
        Product p = new Product(id, name, category, price, active);
        DataRepository.getInstance().addProduct(p);

        if (onSaveCallback != null) onSaveCallback.run();
        dispose();
    }
}
