package com.chatroom.gui.client;

import com.chatroom.gui.Theme;
import com.chatroom.gui.ThemeManager;
import com.chatroom.gui.client.components.ChatAreaPanel;
import com.chatroom.gui.client.components.UserListPanel;
import com.chatroom.i18n.Language;
import com.chatroom.i18n.LanguageManager;
import com.chatroom.net.ChatClient;
import com.chatroom.net.ChatMessage;
import com.chatroom.net.MessageType;
import com.chatroom.util.TextFileExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main chat window shown after a successful connection: room label, chat
 * transcript, message composer, online-user sidebar (double-click for a
 * private message), and a menu bar offering room switching, log export,
 * language/theme selection and a sound-notification toggle.
 */
public class ClientWindow extends JFrame implements LanguageManager.LanguageChangeListener,
        ThemeManager.ThemeChangeListener, ChatClient.ClientListener {

    private final ChatClient client;
    private final String username;
    private String currentRoom;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu roomMenu;
    private JMenu languageMenu;
    private JMenu themeMenu;
    private JMenu helpMenu;
    private JMenuItem saveLogItem;
    private JMenuItem exitItem;
    private JMenuItem changeRoomItem;
    private JMenuItem aboutItem;
    private JCheckBoxMenuItem notificationsItem;

    private final JLabel roomLabel = new JLabel();
    private final ChatAreaPanel chatArea = new ChatAreaPanel();
    private final UserListPanel userListPanel;
    private final JTextField messageField = new JTextField();
    private final JButton sendButton = new JButton();
    private final JButton disconnectButton = new JButton();
    private final JLabel statusLabel = new JLabel();

    public ClientWindow(ChatClient client, String username, String room) {
        this.client = client;
        this.username = username;
        this.currentRoom = room;

        userListPanel = new UserListPanel(this::onPrivateMessageRequested);

        LanguageManager.getInstance().addListener(this);
        ThemeManager.getInstance().addListener(this);
        client.setListener(this);

        initComponents();
        applyLabels();
        applyOrientation(LanguageManager.getInstance().getCurrentLanguage());

        chatArea.appendSystemLine(fill(LanguageManager.getInstance().t("chat.youJoined"), "room", room));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                client.disconnect();
                System.exit(0);
            }
        });
        setSize(920, 640);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 480));
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        roomLabel.setFont(roomLabel.getFont().deriveFont(Font.BOLD, 16f));
        topBar.add(roomLabel, BorderLayout.WEST);
        content.add(topBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(1.0);
        splitPane.setLeftComponent(chatArea);
        splitPane.setRightComponent(userListPanel);
        content.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));
        messageField.addActionListener(e -> onSend());
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        sendButton.addActionListener(e -> onSend());
        disconnectButton.addActionListener(e -> onDisconnect());
        buttonsRow.add(sendButton);
        buttonsRow.add(disconnectButton);
        bottomPanel.add(buttonsRow, BorderLayout.EAST);

        JPanel southWrap = new JPanel(new BorderLayout(4, 4));
        southWrap.add(bottomPanel, BorderLayout.NORTH);
        statusLabel.setForeground(Color.GRAY);
        southWrap.add(statusLabel, BorderLayout.SOUTH);
        content.add(southWrap, BorderLayout.SOUTH);

        setContentPane(content);

        buildMenuBar();
        setJMenuBar(menuBar);
    }

    private void buildMenuBar() {
        menuBar = new JMenuBar();
        LanguageManager lm = LanguageManager.getInstance();

        fileMenu = new JMenu();
        saveLogItem = new JMenuItem();
        saveLogItem.addActionListener(e -> onSaveLog());
        exitItem = new JMenuItem();
        exitItem.addActionListener(e -> onDisconnect());
        fileMenu.add(saveLogItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        roomMenu = new JMenu();
        changeRoomItem = new JMenuItem();
        changeRoomItem.addActionListener(e -> onChangeRoom());
        notificationsItem = new JCheckBoxMenuItem();
        notificationsItem.setSelected(true);
        roomMenu.add(changeRoomItem);
        roomMenu.addSeparator();
        roomMenu.add(notificationsItem);

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

        menuBar.add(fileMenu);
        menuBar.add(roomMenu);
        menuBar.add(languageMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);
    }

    private void onSend() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        client.send(ChatMessage.chat(username, currentRoom, text));
        chatArea.appendChatLine(username, text, true);
        messageField.setText("");
    }

    private void onPrivateMessageRequested(String targetUser) {
        if (targetUser.equals(username)) {
            return;
        }
        LanguageManager lm = LanguageManager.getInstance();
        String message = JOptionPane.showInputDialog(this, lm.t("client.sendPrivate") + " -> " + targetUser);
        if (message != null && !message.trim().isEmpty()) {
            client.send(ChatMessage.privateChat(username, targetUser, message.trim()));
        }
    }

    private void onChangeRoom() {
        LanguageManager lm = LanguageManager.getInstance();
        String newRoom = JOptionPane.showInputDialog(this, lm.t("dialog.roomNamePrompt"), currentRoom);
        if (newRoom != null && !newRoom.trim().isEmpty() && !newRoom.trim().equals(currentRoom)) {
            client.send(ChatMessage.changeRoom(newRoom.trim()));
        }
    }

    private void onDisconnect() {
        LanguageManager lm = LanguageManager.getInstance();
        int confirm = JOptionPane.showConfirmDialog(this, lm.t("dialog.confirmDisconnect"),
                lm.t("dialog.confirmTitle"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            client.disconnect();
            System.exit(0);
        }
    }

    private void onSaveLog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("chat-log.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                new TextFileExporter().export(chatArea.getPlainText(), chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void onAbout() {
        LanguageManager lm = LanguageManager.getInstance();
        JOptionPane.showMessageDialog(this, lm.t("dialog.aboutText"), lm.t("dialog.aboutTitle"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String fill(String template, String key, String value) {
        return template.replace("{" + key + "}", value);
    }

    private String fillUser(String template, String user) {
        return template.replace("{user}", user);
    }

    // ----- ChatClient.ClientListener callbacks (invoked on the network thread) -----

    @Override
    public void onMessage(ChatMessage message) {
        SwingUtilities.invokeLater(() -> handleMessage(message));
    }

    @Override
    public void onDisconnected(String reason) {
        SwingUtilities.invokeLater(() -> {
            LanguageManager lm = LanguageManager.getInstance();
            statusLabel.setText(lm.t("client.status.disconnected"));
            sendButton.setEnabled(false);
            messageField.setEnabled(false);
        });
    }

    private void handleMessage(ChatMessage message) {
        LanguageManager lm = LanguageManager.getInstance();
        switch (message.getType()) {
            case CHAT:
                if (!username.equals(message.getSender())) {
                    chatArea.appendChatLine(message.getSender(), message.getContent(), false);
                    beepIfEnabled();
                }
                break;
            case PRIVATE_CHAT:
                String label = username.equals(message.getSender())
                        ? fillUser(lm.t("chat.privateTo"), message.getRecipient())
                        : fillUser(lm.t("chat.privateFrom"), message.getSender());
                chatArea.appendPrivateLine(label, message.getContent());
                if (!username.equals(message.getSender())) {
                    beepIfEnabled();
                }
                break;
            case USER_JOINED:
                chatArea.appendSystemLine(fillUser(lm.t("chat.joined"), message.getSender()));
                break;
            case USER_LEFT:
                chatArea.appendSystemLine(fillUser(lm.t("chat.left"), message.getSender()));
                break;
            case USER_LIST:
                userListPanel.setUsers(message.getUserList());
                break;
            case SERVER_NOTICE:
                chatArea.appendServerNotice(lm.t("chat.serverNotice"), message.getContent());
                beepIfEnabled();
                break;
            case JOIN_ACCEPTED:
                currentRoom = message.getRoom();
                roomLabel.setText(lm.t("client.currentRoom") + ": " + currentRoom);
                chatArea.appendSystemLine(fill(lm.t("chat.youJoined"), "room", currentRoom));
                break;
            case JOIN_REJECTED:
                JOptionPane.showMessageDialog(this, lm.t("login.error.usernameTaken"));
                break;
            case KICKED:
                chatArea.appendServerNotice(lm.t("chat.serverNotice"), lm.t("chat.kicked"));
                sendButton.setEnabled(false);
                messageField.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void beepIfEnabled() {
        if (notificationsItem != null && notificationsItem.isSelected()) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.t("client.title") + " - " + username);
        roomLabel.setText(lm.t("client.currentRoom") + ": " + currentRoom);
        sendButton.setText(lm.t("client.send"));
        disconnectButton.setText(lm.t("client.disconnect"));
        statusLabel.setText(lm.t("client.status.connected"));

        fileMenu.setText(lm.t("menu.file"));
        saveLogItem.setText(lm.t("menu.file.saveLog"));
        exitItem.setText(lm.t("menu.file.exit"));
        roomMenu.setText(lm.t("client.currentRoom"));
        changeRoomItem.setText(lm.t("client.newRoom"));
        notificationsItem.setText(lm.t("client.notifications"));

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

        userListPanel.refreshLabel();
    }

    private void applyOrientation(Language language) {
        ComponentOrientation orientation = language.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;
        applyComponentOrientation(orientation);
        if (menuBar != null) {
            menuBar.applyComponentOrientation(orientation);
        }
        chatArea.applyOrientation(orientation);
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
