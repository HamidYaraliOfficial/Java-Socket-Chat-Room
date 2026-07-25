package com.chatroom.gui;

import com.chatroom.gui.client.LoginDialog;
import com.chatroom.gui.server.ServerWindow;
import com.chatroom.i18n.Language;
import com.chatroom.i18n.LanguageManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * First window shown to the user: lets them choose whether this instance
 * of the application should host a chat server or connect to one as a
 * client. Both roles can be launched from the same jar.
 */
public class LauncherWindow extends JFrame implements LanguageManager.LanguageChangeListener,
        ThemeManager.ThemeChangeListener {

    private JMenuBar menuBar;
    private JMenu languageMenu;
    private JMenu themeMenu;
    private JMenu helpMenu;
    private JMenuItem aboutItem;

    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();
    private final JButton serverButton = new JButton();
    private final JButton clientButton = new JButton();
    private final JLabel serverDescLabel = new JLabel();
    private final JLabel clientDescLabel = new JLabel();

    public LauncherWindow() {
        LanguageManager.getInstance().addListener(this);
        ThemeManager.getInstance().addListener(this);

        initComponents();
        applyLabels();
        applyOrientation(LanguageManager.getInstance().getCurrentLanguage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 26f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(6));

        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(Color.GRAY);
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(30));

        JPanel serverCard = buildCard(serverButton, serverDescLabel);
        JPanel clientCard = buildCard(clientButton, clientDescLabel);

        serverButton.addActionListener(e -> onStartServer());
        clientButton.addActionListener(e -> onStartClient());

        content.add(serverCard);
        content.add(Box.createVerticalStrut(16));
        content.add(clientCard);

        setContentPane(content);

        buildMenuBar();
        setJMenuBar(menuBar);
    }

    private JPanel buildCard(JButton button, JLabel descLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), new EmptyBorder(14, 14, 14, 14)));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(460, 100));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 15f));
        button.setMaximumSize(new Dimension(300, 40));

        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(Color.GRAY);

        card.add(button);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        return card;
    }

    private void buildMenuBar() {
        menuBar = new JMenuBar();
        LanguageManager lm = LanguageManager.getInstance();

        languageMenu = new JMenu();
        ButtonGroup languageGroup = new ButtonGroup();
        for (Language lang : Language.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(lang.getDisplayName());
            item.setSelected(lang == lm.getCurrentLanguage());
            item.addActionListener(e -> lm.setLanguage(lang));
            languageGroup.add(item);
            languageMenu.add(item);
        }

        themeMenu = new JMenu();
        ButtonGroup themeGroup = new ButtonGroup();
        for (Theme theme : Theme.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem();
            item.setSelected(theme == ThemeManager.getInstance().getCurrentTheme());
            item.addActionListener(e -> ThemeManager.getInstance().applyTheme(theme));
            item.putClientProperty("themeRef", theme);
            themeGroup.add(item);
            themeMenu.add(item);
        }

        helpMenu = new JMenu();
        aboutItem = new JMenuItem();
        aboutItem.addActionListener(e -> onAbout());
        helpMenu.add(aboutItem);

        menuBar.add(languageMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);
    }

    private void onAbout() {
        LanguageManager lm = LanguageManager.getInstance();
        JOptionPane.showMessageDialog(this, lm.t("dialog.aboutText"), lm.t("dialog.aboutTitle"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onStartServer() {
        ServerWindow window = new ServerWindow();
        window.setVisible(true);
        dispose();
    }

    private void onStartClient() {
        LoginDialog dialog = new LoginDialog(null);
        dialog.setVisible(true);
        if (dialog.isConnected()) {
            dispose();
        }
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.t("launcher.title"));
        titleLabel.setText(lm.t("launcher.title"));
        subtitleLabel.setText(lm.t("launcher.subtitle"));
        serverButton.setText(lm.t("launcher.server"));
        clientButton.setText(lm.t("launcher.client"));
        serverDescLabel.setText(lm.t("launcher.serverDesc"));
        clientDescLabel.setText(lm.t("launcher.clientDesc"));

        languageMenu.setText(lm.t("menu.language"));
        themeMenu.setText(lm.t("menu.theme"));
        for (Component c : themeMenu.getMenuComponents()) {
            if (c instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem) c;
                Theme theme = (Theme) item.getClientProperty("themeRef");
                if (theme != null) {
                    item.setText(lm.t(theme.getLabelKey()));
                }
            }
        }
        helpMenu.setText(lm.t("menu.help"));
        aboutItem.setText(lm.t("menu.help.about"));
    }

    private void applyOrientation(Language language) {
        ComponentOrientation orientation = language.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;
        applyComponentOrientation(orientation);
        if (menuBar != null) {
            menuBar.applyComponentOrientation(orientation);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void onLanguageChanged(Language newLanguage) {
        applyLabels();
        applyOrientation(newLanguage);
    }

    @Override
    public void onThemeChanged(Theme newTheme) {
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
}
