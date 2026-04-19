/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.saidera_project;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.saidera_project.ui.LoginScreen;
import com.mycompany.saidera_project.ui.UIPalette;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Saidera_project {

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
