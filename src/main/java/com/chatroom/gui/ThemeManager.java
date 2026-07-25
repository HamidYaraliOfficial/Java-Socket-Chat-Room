package com.chatroom.gui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Applies the selected {@link Theme} to the whole Swing UIManager so that
 * every component created afterwards automatically inherits the palette.
 * Also keeps a registry of listeners so already-built windows can repaint
 * themselves instantly when the theme changes at runtime.
 */
public final class ThemeManager {

    public interface ThemeChangeListener {
        void onThemeChanged(Theme newTheme);
    }

    private static final ThemeManager INSTANCE = new ThemeManager();
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private final List<ThemeChangeListener> listeners = new ArrayList<>();

    private Theme currentTheme;

    private ThemeManager() {
        String saved = prefs.get("app.theme", Theme.WINDOWS_11.name());
        Theme t;
        try {
            t = Theme.valueOf(saved);
        } catch (IllegalArgumentException e) {
            t = Theme.WINDOWS_11;
        }
        currentTheme = t;
    }

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void addListener(ThemeChangeListener l) {
        listeners.add(l);
    }

    public void removeListener(ThemeChangeListener l) {
        listeners.remove(l);
    }

    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        prefs.put("app.theme", theme.name());

        UIManager.put("control", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("info", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("nimbusBase", new ColorUIResource(theme.getAccent()));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("nimbusLightBackground", new ColorUIResource(theme.getPanelBackground()));

        UIManager.put("Panel.background", new ColorUIResource(theme.getBackground()));
        UIManager.put("OptionPane.background", new ColorUIResource(theme.getBackground()));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(theme.getForeground()));

        UIManager.put("Button.background", new ColorUIResource(theme.getAccent()));
        UIManager.put("Button.foreground", new ColorUIResource(theme.getAccentForeground()));
        UIManager.put("Button.select", new ColorUIResource(theme.getAccent().darker()));
        UIManager.put("Button.focus", new ColorUIResource(theme.getAccent()));

        UIManager.put("Label.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("CheckBox.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("CheckBox.background", new ColorUIResource(theme.getBackground()));
        UIManager.put("RadioButton.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("RadioButton.background", new ColorUIResource(theme.getBackground()));

        UIManager.put("TextField.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("TextField.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("TextField.caretForeground", new ColorUIResource(theme.getForeground()));
        UIManager.put("TextArea.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("TextArea.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("PasswordField.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("PasswordField.foreground", new ColorUIResource(theme.getForeground()));

        UIManager.put("ComboBox.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("ComboBox.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(theme.getSelectionBackground()));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(theme.getForeground()));

        UIManager.put("Table.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("Table.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("Table.selectionBackground", new ColorUIResource(theme.getSelectionBackground()));
        UIManager.put("Table.selectionForeground", new ColorUIResource(theme.getForeground()));
        UIManager.put("Table.gridColor", new ColorUIResource(theme.getBorder()));
        UIManager.put("TableHeader.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("TableHeader.foreground", new ColorUIResource(theme.getForeground()));

        UIManager.put("TabbedPane.background", new ColorUIResource(theme.getBackground()));
        UIManager.put("TabbedPane.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("TabbedPane.selected", new ColorUIResource(theme.getSelectionBackground()));
        UIManager.put("TabbedPane.contentAreaColor", new ColorUIResource(theme.getPanelBackground()));

        UIManager.put("ProgressBar.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("ProgressBar.foreground", new ColorUIResource(theme.getAccent()));

        UIManager.put("MenuBar.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("Menu.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("Menu.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("MenuItem.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("MenuItem.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(theme.getSelectionBackground()));

        UIManager.put("ScrollPane.background", new ColorUIResource(theme.getBackground()));
        UIManager.put("Viewport.background", new ColorUIResource(theme.getPanelBackground()));

        UIManager.put("TitledBorder.titleColor", new ColorUIResource(theme.getForeground()));

        UIManager.put("List.background", new ColorUIResource(theme.getPanelBackground()));
        UIManager.put("List.foreground", new ColorUIResource(theme.getForeground()));
        UIManager.put("List.selectionBackground", new ColorUIResource(theme.getSelectionBackground()));
        UIManager.put("List.selectionForeground", new ColorUIResource(theme.getForeground()));

        UIManager.put("SplitPane.background", new ColorUIResource(theme.getBackground()));

        for (ThemeChangeListener l : listeners) {
            l.onThemeChanged(theme);
        }
    }

    /** Recursively updates every component in the given container tree. */
    public static void updateComponentTreeUI(Component component) {
        SwingUtilities.updateComponentTreeUI(component);
    }

    public void applyGlobalFont(Font font) {
        FontUIResource fontResource = new FontUIResource(font);
        for (Object key : UIManager.getLookAndFeelDefaults().keySet().toArray()) {
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontResource);
            }
        }
    }
}
