package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ProductForm extends BaseDialog {
    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private DefaultComboBoxModel<String> categoryModel;
    private JSpinner priceSpinner;
    private JComboBox<Object> stockLinkCombo;
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

        List<String> categories = DataRepository.getInstance().getProductCategories();
        categoryModel = new DefaultComboBoxModel<>(categories.toArray(new String[0]));
        categoryCombo = new JComboBox<>(categoryModel);
        categoryCombo.setEditable(true);
        categoryCombo.setPreferredSize(new Dimension(0, 40));

        JButton addCategoryBtn = new JButton("+ Nova Categoria");
        addCategoryBtn.setFont(UIPalette.FONT_LABEL.deriveFont(11f));
        addCategoryBtn.setFocusPainted(false);
        addCategoryBtn.addActionListener(this::addNewCategory);

        JPanel categoryPanel = new JPanel(new BorderLayout(10, 0));
        categoryPanel.setOpaque(false);
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        categoryPanel.add(addCategoryBtn, BorderLayout.EAST);

        addField("CATEGORIA", categoryPanel, gbc, 1);

        priceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 10000.00, 0.50));
        priceSpinner.setPreferredSize(new Dimension(0, 40));
        addField("PREÇO (R$)", priceSpinner, gbc, 2);

        // Stock Link
        java.util.List<com.mycompany.saidera_project.models.StockItem> items = DataRepository.getInstance()
                .getInventory();
        DefaultComboBoxModel<Object> comboModel = new DefaultComboBoxModel<>();
        comboModel.addElement("Nenhum / Serviço");
        for (com.mycompany.saidera_project.models.StockItem item : items)
            comboModel.addElement(item);

        stockLinkCombo = new JComboBox<>(comboModel);
        stockLinkCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof com.mycompany.saidera_project.models.StockItem) {
                    setText(((com.mycompany.saidera_project.models.StockItem) value).getName());
                }
                return this;
            }
        });
        stockLinkCombo.setPreferredSize(new Dimension(0, 40));
        addField("VINCULAR AO ESTOQUE", stockLinkCombo, gbc, 3);

        activeBox = new JCheckBox("Produto Ativo no Cardápio", true);
        activeBox.setFont(UIPalette.FONT_BODY);
        gbc.gridy = 7;
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
        Object selectedCategory = categoryCombo.getSelectedItem();
        String category = selectedCategory != null ? selectedCategory.toString().trim() : "";
        double price = (Double) priceSpinner.getValue();
        boolean active = activeBox.isSelected();

        if (name.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
            return;
        }

        String id = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 28);

        String linkedStockId = null;
        Object selected = stockLinkCombo.getSelectedItem();
        if (selected instanceof com.mycompany.saidera_project.models.StockItem) {
            linkedStockId = ((com.mycompany.saidera_project.models.StockItem) selected).getId();
        }

        Product p = new Product(id, name, category, price, active, linkedStockId);
        DataRepository.getInstance().addProduct(p);

        if (onSaveCallback != null)
            onSaveCallback.run();
        dispose();
    }

    private void addNewCategory(ActionEvent e) {
        String categoryName = JOptionPane.showInputDialog(this, "Digite o nome da nova categoria:", "Nova Categoria",
                JOptionPane.PLAIN_MESSAGE);

        if (categoryName == null) {
            return;
        }

        String normalized = categoryName.trim();
        if (normalized.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe um nome válido para a categoria.");
            return;
        }

        for (int i = 0; i < categoryModel.getSize(); i++) {
            String existing = categoryModel.getElementAt(i);
            if (existing != null && existing.equalsIgnoreCase(normalized)) {
                categoryCombo.setSelectedItem(existing);
                JOptionPane.showMessageDialog(this, "Essa categoria já existe.");
                return;
            }
        }

        categoryModel.addElement(normalized);
        categoryCombo.setSelectedItem(normalized);
    }
}
