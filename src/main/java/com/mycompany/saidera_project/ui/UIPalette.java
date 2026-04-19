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
    
    // Background Colors
    public static final Color BACKGROUND = new Color(0xF7F9FF);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SLATE = new Color(0x203243); // Sidebar / Navigation
    
    // Text Colors
    public static final Color ON_BACKGROUND = new Color(0x091D2E);
    public static final Color ON_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(0x504532);
    
    // Status Colors
    public static final Color ERROR = new Color(0xBA1A1A);
    public static final Color SUCCESS = new Color(0x2E7D32);
    
    // UI Helpers
    public static final int ROUNDNESS = 12;

    // Fonts (Using standard names, FlatLaf will improve rendering)
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
}
