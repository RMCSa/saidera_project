package com.mycompany.saidera_project.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Design System tokens for Saidera Desktop based on "Amber Ledger" Stitch
 * Design System.
 */
public class UIPalette {
    // Primary Colors
    public static final Color AMBER = new Color(0xFFBF00);
    public static final Color AMBER_DARK = new Color(0x795900);
    public static final Color AMBER_LIGHT = new Color(0xFFF8DC);
    
    // Background Colors
    public static Color BACKGROUND = new Color(0xF0F4FF);
    public static Color SURFACE = Color.WHITE;
    public static Color SURFACE_2 = new Color(0xF8FAFC);
    public static final Color SLATE = new Color(0x203243); // Sidebar / Navigation (sempre escuro)
    public static final Color SLATE_LIGHT = new Color(0x2C4259); // Sidebar Hover (sempre escuro)
    public static Color BORDER = new Color(0xE2E8F0);
    
    // Text Colors
    public static Color ON_BACKGROUND = new Color(0x091D2E);
    public static final Color ON_PRIMARY = Color.WHITE;
    public static final Color ON_AMBER = new Color(0x091D2E); // Dark text for Amber backgrounds
    public static Color TEXT_SECONDARY = new Color(0x504532);
    
    // Status Colors
    public static final Color ERROR = new Color(0xDC2626);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color WARNING = new Color(0xD97706);
    public static final Color INFO = new Color(0x2563EB);

    static {
        updateTheme();
    }

    public static void updateTheme() {
        if (com.formdev.flatlaf.FlatLaf.isLafDark()) {
            BACKGROUND = new Color(0x0F172A); // slate-900
            SURFACE = new Color(0x1E293B);    // slate-800
            SURFACE_2 = new Color(0x334155);  // slate-700
            BORDER = new Color(0x334155);     // slate-700
            ON_BACKGROUND = new Color(0xF8FAFC); // slate-50
            TEXT_SECONDARY = new Color(0x94A3B8); // slate-400
        } else {
            BACKGROUND = new Color(0xF0F4FF);
            SURFACE = Color.WHITE;
            SURFACE_2 = new Color(0xF8FAFC);
            BORDER = new Color(0xE2E8F0);
            ON_BACKGROUND = new Color(0x091D2E);
            TEXT_SECONDARY = new Color(0x504532);
        }
    }
    
    // UI Helpers
    public static final int ROUNDNESS = 12;

    // Fonts (Using standard names, FlatLaf will improve rendering)
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
}
