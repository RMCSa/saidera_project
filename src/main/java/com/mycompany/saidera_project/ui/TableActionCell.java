package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom table cell with Edit and Delete buttons.
 */
public class TableActionCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private final JPanel panel;
    private final JButton editBtn;
    private final JButton deleteBtn;
    private final ActionListener onEdit;
    private final ActionListener onDelete;
    private int editingRow = -1;
    private JTable editingTable;

    public TableActionCell(ActionListener onEdit, ActionListener onDelete) {
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(true);

        editBtn = new JButton("✎");
        editBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        editBtn.setToolTipText("Editar");
        editBtn.setBackground(new Color(0xEFF6FF));
        editBtn.setForeground(new Color(0x2563EB));
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(0x93C5FD)));
        editBtn.addActionListener(e -> {
            fireEditingStopped();
            if (this.onEdit != null && editingRow >= 0) {
                this.onEdit.actionPerformed(new ActionEvent(editingTable != null ? editingTable : this,
                        ActionEvent.ACTION_PERFORMED, String.valueOf(editingRow)));
            }
        });

        deleteBtn = new JButton("🗑");
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        deleteBtn.setToolTipText("Excluir");
        deleteBtn.setBackground(new Color(0xFEF2F2));
        deleteBtn.setForeground(UIPalette.ERROR);
        deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFECACA)));
        deleteBtn.addActionListener(e -> {
            fireEditingStopped();
            if (this.onDelete != null && editingRow >= 0) {
                this.onDelete.actionPerformed(new ActionEvent(editingTable != null ? editingTable : this,
                        ActionEvent.ACTION_PERFORMED, String.valueOf(editingRow)));
            }
        });

        panel.add(editBtn);
        panel.add(deleteBtn);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }
        
        // Estilo dinâmico baseado no tema ativo
        if (com.formdev.flatlaf.FlatLaf.isLafDark()) {
            editBtn.setBackground(new Color(0x1E3A8A));
            editBtn.setForeground(new Color(0x60A5FA));
            editBtn.setBorder(BorderFactory.createLineBorder(new Color(0x3B82F6)));
            
            deleteBtn.setBackground(new Color(0x7F1D1D));
            deleteBtn.setForeground(new Color(0xFCA5A5));
            deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(0xEF4444)));
        } else {
            editBtn.setBackground(new Color(0xEFF6FF));
            editBtn.setForeground(new Color(0x2563EB));
            editBtn.setBorder(BorderFactory.createLineBorder(new Color(0x93C5FD)));
            
            deleteBtn.setBackground(new Color(0xFEF2F2));
            deleteBtn.setForeground(UIPalette.ERROR);
            deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFECACA)));
        }
        
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.editingRow = row;
        this.editingTable = table;
        
        // Estilo dinâmico baseado no tema ativo
        if (com.formdev.flatlaf.FlatLaf.isLafDark()) {
            editBtn.setBackground(new Color(0x1E3A8A));
            editBtn.setForeground(new Color(0x60A5FA));
            editBtn.setBorder(BorderFactory.createLineBorder(new Color(0x3B82F6)));
            
            deleteBtn.setBackground(new Color(0x7F1D1D));
            deleteBtn.setForeground(new Color(0xFCA5A5));
            deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(0xEF4444)));
        } else {
            editBtn.setBackground(new Color(0xEFF6FF));
            editBtn.setForeground(new Color(0x2563EB));
            editBtn.setBorder(BorderFactory.createLineBorder(new Color(0x93C5FD)));
            
            deleteBtn.setBackground(new Color(0xFEF2F2));
            deleteBtn.setForeground(UIPalette.ERROR);
            deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFECACA)));
        }
        
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    public void updateUI() {
        if (panel != null) panel.updateUI();
        if (editBtn != null) editBtn.updateUI();
        if (deleteBtn != null) deleteBtn.updateUI();
    }
}
