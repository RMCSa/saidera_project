package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.StockItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog for registering stock entry or exit.
 */
public class StockTransactionForm extends BaseDialog {

    private JComboBox<StockItem> itemCombo;
    private JTextField newItemNameField;
    private JTextField newItemUnitField;
    private JSpinner newItemMinLevelField;
    private JSpinner quantitySpinner;
    private JTextArea observationArea;
    private JCheckBox newItemToggle;
    private JPanel newFieldsPanel;
    private JLabel currentStockLabel;
    private JComponent existingItemField;
    private JLabel existingItemLabel;
    private boolean isEntry;
    private Runnable onSaveCallback;

    public StockTransactionForm(Frame owner, boolean isEntry, Runnable onSaveCallback) {
        super(owner, isEntry ? "Registrar Entrada de Estoque" : "Registrar Saída de Estoque", 500, 650);
        this.isEntry = isEntry;
        this.onSaveCallback = onSaveCallback;

        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Header Label in Dialog
        JLabel headerLabel = new JLabel(isEntry ? "📥 Entrada de Material" : "📤 Saída de Insumo");
        headerLabel.setFont(UIPalette.FONT_TITLE);
        headerLabel.setForeground(isEntry ? new Color(0x2E7D32) : UIPalette.ERROR);
        gbc.gridy = 0;
        contentPanel.add(headerLabel, gbc);

        // Toggle for New Item
        newItemToggle = new JCheckBox("Cadastrar novo item no sistema");
        newItemToggle.setFont(UIPalette.FONT_BODY);
        gbc.gridy = 1;
        contentPanel.add(newItemToggle, gbc);

        // Product Selector (for existing)
        List<StockItem> items = DataRepository.getInstance().getInventory();
        itemCombo = new JComboBox<>(items.toArray(new StockItem[0]));
        itemCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof StockItem) {
                    setText(((StockItem) value).getName());
                }
                return this;
            }
        });

        JPanel itemSelectorPanel = new JPanel(new BorderLayout(0, 6));
        itemSelectorPanel.setOpaque(false);
        itemCombo.setPreferredSize(new Dimension(0, 45));
        itemSelectorPanel.add(itemCombo, BorderLayout.NORTH);

        currentStockLabel = new JLabel();
        currentStockLabel.setFont(UIPalette.FONT_LABEL.deriveFont(Font.PLAIN, 11f));
        currentStockLabel.setForeground(Color.GRAY);
        itemSelectorPanel.add(currentStockLabel, BorderLayout.SOUTH);

        // Capture label + field components so we can hide/show them when toggling "new
        // item"
        int before = contentPanel.getComponentCount();
        addField("SELECIONE O PRODUTO", itemSelectorPanel, gbc, 2);
        existingItemLabel = (JLabel) contentPanel.getComponent(before);
        existingItemField = (JComponent) contentPanel.getComponent(before + 1);

        updateCurrentStockLabel();
        itemCombo.addActionListener(e -> updateCurrentStockLabel());

        // New Item Fields (Initial hidden)
        newFieldsPanel = new JPanel(new GridBagLayout());
        newFieldsPanel.setOpaque(false);
        newFieldsPanel.setVisible(false);
        GridBagConstraints gbcN = new GridBagConstraints();
        gbcN.fill = GridBagConstraints.HORIZONTAL;
        gbcN.weightx = 1.0;
        gbcN.gridx = 0;

        newItemNameField = new JTextField();
        newItemNameField.setPreferredSize(new Dimension(0, 45));
        addField("NOME DO NOVO PRODUTO", newItemNameField, gbcN, 0, newFieldsPanel);

        newItemUnitField = new JTextField("unid.");
        newItemUnitField.setPreferredSize(new Dimension(0, 45));
        addField("UNIDADE (ex: kg, barril)", newItemUnitField, gbcN, 1, newFieldsPanel);

        newItemMinLevelField = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 1));
        newItemMinLevelField.setPreferredSize(new Dimension(0, 45));
        addField("NÍVEL MÍNIMO (ALERTA)", newItemMinLevelField, gbcN, 2, newFieldsPanel);

        gbc.gridy = 3;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(newFieldsPanel, gbc);

        newItemToggle.addActionListener(e -> {
            boolean selected = newItemToggle.isSelected();
            itemCombo.setEnabled(!selected);
            if (existingItemLabel != null)
                existingItemLabel.setVisible(!selected);
            if (existingItemField != null)
                existingItemField.setVisible(!selected);
            newFieldsPanel.setVisible(selected);
            revalidate();
            repaint();
        });

        // Quantity
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        quantitySpinner.setPreferredSize(new Dimension(0, 45));
        addField("QUANTIDADE (" + (isEntry ? "Adicionar" : "Remover") + ")", quantitySpinner, gbc, 4);

        // Observations
        observationArea = new JTextArea(3, 20);
        observationArea.setFont(UIPalette.FONT_BODY);
        observationArea.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        addField("OBSERVAÇÕES (OPCIONAL)", new JScrollPane(observationArea), gbc, 5);

        // Buttons
        JButton cancelBtn = createSecondaryButton("Cancelar");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createPrimaryButton(isEntry ? "Confirmar Entrada" : "Confirmar Saída");
        if (!isEntry)
            saveBtn.setBackground(UIPalette.ERROR);
        saveBtn.addActionListener(this::handleSave);

        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);
    }

    public StockTransactionForm(Frame owner, boolean isEntry, Runnable onSaveCallback, StockItem preselectedItem) {
        this(owner, isEntry, onSaveCallback);
        if (preselectedItem != null) {
            newItemToggle.setSelected(false);
            newItemToggle.setVisible(false);
            for (int i = 0; i < itemCombo.getItemCount(); i++) {
                StockItem cItem = itemCombo.getItemAt(i);
                if (cItem.getId().equals(preselectedItem.getId())) {
                    itemCombo.setSelectedIndex(i);
                    break;
                }
            }
            updateCurrentStockLabel();
        }
    }

    private void handleSave(ActionEvent e) {
        int quantity = (Integer) quantitySpinner.getValue();
        int delta = isEntry ? quantity : -quantity;
        String itemId = null;

        if (newItemToggle.isSelected()) {
            String name = newItemNameField.getText();
            String unit = newItemUnitField.getText();
            int minLevel = (Integer) newItemMinLevelField.getValue();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do novo item.");
                return;
            }

            itemId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 28);
            StockItem newItem = new StockItem(itemId, name, 0, minLevel, unit);
            DataRepository.getInstance().addStockItem(newItem);
        } else {
            StockItem selected = (StockItem) itemCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.");
                return;
            }
            itemId = selected.getId();
        }

        boolean success = DataRepository.getInstance().updateStock(itemId, delta);

        if (success) {
            if (onSaveCallback != null)
                onSaveCallback.run();
            dispose();
        } else {
            StockItem item = DataRepository.getInstance().getStockItemById(itemId);
            JOptionPane.showMessageDialog(this,
                    "Saldo insuficiente para realizar esta saída!\nEstoque atual: "
                            + (item != null ? item.getCurrentLevel() : 0),
                    "Erro de Estoque",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCurrentStockLabel() {
        StockItem selected = (StockItem) itemCombo.getSelectedItem();
        if (selected == null) {
            currentStockLabel.setText(" ");
            return;
        }
        currentStockLabel.setText("Estoque atual: " + selected.getCurrentLevel() + " " + selected.getUnit());
    }
}
