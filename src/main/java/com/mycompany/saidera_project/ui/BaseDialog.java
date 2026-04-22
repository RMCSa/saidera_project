package com.mycompany.saidera_project.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Classe base padronizada e reutilizável para componentes de diálogo e popups da interface gráfica.
 * Mantém o alinhamento visual com o Design System definido pelo UIPalette.
 */
public class BaseDialog extends JDialog {
    /** Painel de conteúdo central para as caixas de entrada ou informações do diálogo. */
    protected JPanel contentPanel;
    /** Painel de rodapé, comumente contendo os botões de ação final. */
    protected JPanel footerPanel;

    /**
     * Construtor de pré-definição da janela base.
     * 
     * @param owner A janela em foco base que invocou a caixa.
     * @param title O título do popup na barra de ferramentas.
     * @param width Escala dimensional horizontal.
     * @param height Escala dimensional vertical.
     */
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

    /**
     * Adiciona um campo pareado com um texto (Label) no painel de base usando as travas de limite em Grid.
     * 
     * @param label Rótulo do campo textual.
     * @param field Objeto interativo contendo input dos dados.
     * @param gbc Constraints dimensionais em grade.
     * @param row O local de índice da nova linha.
     */
    protected void addField(String label, JComponent field, GridBagConstraints gbc, int row) {
        addField(label, field, gbc, row, contentPanel);
    }

    /**
     * Adiciona um campo de formulário pareado sobre contêiner específico.
     * 
     * @param label Constante orientativa de texto de entrada.
     * @param field Controle interativo para o usuário.
     * @param gbc Regras lógicas base de grade de componente da UI.
     * @param row Linha pretendida para posicionar em grade.
     * @param container Receptáculo visual onde renderizar os elementos em conjunto.
     */
    protected void addField(String label, JComponent field, GridBagConstraints gbc, int row, Container container) {
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel l = new JLabel(label);
        l.setFont(UIPalette.FONT_LABEL);
        container.add(l, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        container.add(field, gbc);
    }

    /**
     * Produz um botão com formatação visual destacada, sugerido para finalização e salvamentos principais.
     * 
     * @param text Texto a constar dentro do botão em exibição.
     * @return O componente visual de Botão devidamente configurado para uso principal.
     */
    protected JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIPalette.AMBER);
        btn.setForeground(UIPalette.ON_BACKGROUND);
        btn.setFont(UIPalette.FONT_LABEL);
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    /**
     * Produz um botão genérico sugerido para fins secundários (como por exemplo o de cancelamento/desistir).
     * 
     * @param text Letreiro frontal contido dentro da entidade de botão.
     * @return Instancição paralela formatada não atrativa.
     */
    protected JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIPalette.FONT_LABEL);
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }
}
