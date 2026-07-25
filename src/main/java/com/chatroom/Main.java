package com.chatroom;

import com.chatroom.gui.LauncherWindow;
import com.chatroom.gui.ThemeManager;

import javax.swing.*;

/**
 * Application entry point. Sets up the system look and feel, applies the
 * previously saved (or default) theme, and shows the launcher window
 * (choice between hosting a server or connecting as a client) on the
 * Swing event dispatch thread.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the cross-platform default look and feel.
        }

        ThemeManager.getInstance().applyTheme(ThemeManager.getInstance().getCurrentTheme());

        SwingUtilities.invokeLater(() -> {
            LauncherWindow window = new LauncherWindow();
            window.setVisible(true);
        });
    }
}
