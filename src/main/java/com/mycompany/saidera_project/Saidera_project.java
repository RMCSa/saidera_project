/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.saidera_project;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.saidera_project.ui.LoginScreen;
import com.mycompany.saidera_project.ui.UIPalette;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principal responsável por inicializar a aplicação.
 * Configura o LookAndFeel global do sistema e levanta a janela de login inicial.
 */
public class Saidera_project {

    /**
     * Ponto de entrada (entry point) principal do sistema.
     * 
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        // Setup Look and Feel
        FlatLightLaf.setup();
        
        // Apply Global UI customizations from Design System
        UIManager.put("Button.arc", UIPalette.ROUNDNESS);
        UIManager.put("Component.arc", UIPalette.ROUNDNESS);
        UIManager.put("TextComponent.arc", UIPalette.ROUNDNESS);
        UIManager.put("ScrollBar.width", 12);
        
        // Launch Application
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
