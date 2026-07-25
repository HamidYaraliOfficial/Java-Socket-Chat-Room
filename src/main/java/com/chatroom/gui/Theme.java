package com.chatroom.gui;

import java.awt.Color;

/**
 * Enumerates every visual theme supported by the application.
 * Each theme carries its own color palette used across the whole UI.
 */
public enum Theme {

    WINDOWS_11(
            "menu.theme.windows11",
            new Color(0xF3, 0xF3, 0xF3),
            new Color(0xFF, 0xFF, 0xFF),
            new Color(0x20, 0x20, 0x20),
            new Color(0x00, 0x67, 0xC0),
            new Color(0xE5, 0xF1, 0xFB),
            new Color(0xD0, 0xD0, 0xD0),
            new Color(0x00, 0x67, 0xC0)
    ),
    LIGHT(
            "menu.theme.light",
            new Color(0xFA, 0xFA, 0xFA),
            new Color(0xFF, 0xFF, 0xFF),
            new Color(0x1A, 0x1A, 0x1A),
            new Color(0x4A, 0x90, 0xD9),
            new Color(0xEA, 0xF2, 0xFC),
            new Color(0xDD, 0xDD, 0xDD),
            new Color(0x4A, 0x90, 0xD9)
    ),
    DARK(
            "menu.theme.dark",
            new Color(0x20, 0x20, 0x20),
            new Color(0x2B, 0x2B, 0x2B),
            new Color(0xF0, 0xF0, 0xF0),
            new Color(0x3A, 0x9E, 0xF5),
            new Color(0x35, 0x3D, 0x47),
            new Color(0x45, 0x45, 0x45),
            new Color(0x3A, 0x9E, 0xF5)
    ),
    RED(
            "menu.theme.red",
            new Color(0xFC, 0xF2, 0xF2),
            new Color(0xFF, 0xFF, 0xFF),
            new Color(0x2A, 0x10, 0x10),
            new Color(0xC6, 0x28, 0x28),
            new Color(0xFB, 0xE1, 0xE1),
            new Color(0xE8, 0xC5, 0xC5),
            new Color(0xC6, 0x28, 0x28)
    ),
    BLUE(
            "menu.theme.blue",
            new Color(0xF0, 0xF6, 0xFC),
            new Color(0xFF, 0xFF, 0xFF),
            new Color(0x10, 0x1B, 0x2A),
            new Color(0x15, 0x65, 0xC0),
            new Color(0xDD, 0xEB, 0xFA),
            new Color(0xC2, 0xD8, 0xEE),
            new Color(0x15, 0x65, 0xC0)
    );

    private final String labelKey;
    private final Color background;
    private final Color panelBackground;
    private final Color foreground;
    private final Color accent;
    private final Color selectionBackground;
    private final Color border;
    private final Color accentForeground;

    Theme(String labelKey, Color background, Color panelBackground, Color foreground,
          Color accent, Color selectionBackground, Color border, Color accentForeground) {
        this.labelKey = labelKey;
        this.background = background;
        this.panelBackground = panelBackground;
        this.foreground = foreground;
        this.accent = accent;
        this.selectionBackground = selectionBackground;
        this.border = border;
        this.accentForeground = accentForeground;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public Color getBackground() {
        return background;
    }

    public Color getPanelBackground() {
        return panelBackground;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getAccent() {
        return accent;
    }

    public Color getSelectionBackground() {
        return selectionBackground;
    }

    public Color getBorder() {
        return border;
    }

    public Color getAccentForeground() {
        return accentForeground;
    }
}
