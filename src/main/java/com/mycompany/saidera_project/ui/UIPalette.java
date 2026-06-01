package com.mycompany.saidera_project.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Design System tokens for Saíderas Desktop based on "Amber Ledger" Stitch Design System.
 */
public class UIPalette {
    // Primary Colors
    public static final Color AMBER = new Color(0xFFBF00);
    public static final Color AMBER_DARK = new Color(0x795900);
    public static final Color AMBER_LIGHT = new Color(0xFFF8DC);
    
    // Background Colors
    public static final Color BACKGROUND = new Color(0xF0F4FF);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_2 = new Color(0xF8FAFC);
    public static final Color SLATE = new Color(0x203243); // Sidebar / Navigation
    public static final Color SLATE_LIGHT = new Color(0x2C4259); // Sidebar Hover
    public static final Color BORDER = new Color(0xE2E8F0);
    
    // Text Colors
    public static final Color ON_BACKGROUND = new Color(0x091D2E);
    public static final Color ON_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(0x504532);
    
    // Status Colors
    public static final Color ERROR = new Color(0xDC2626);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color WARNING = new Color(0xD97706);
    public static final Color INFO = new Color(0x2563EB);
    
    // UI Helpers
    public static final int ROUNDNESS = 12;

    // Fonts (Using standard names, FlatLaf will improve rendering)
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
}
