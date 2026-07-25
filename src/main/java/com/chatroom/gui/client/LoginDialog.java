package com.chatroom.gui.client;

import com.chatroom.gui.Theme;
import com.chatroom.gui.ThemeManager;
import com.chatroom.i18n.Language;
import com.chatroom.i18n.LanguageManager;
import com.chatroom.net.ChatClient;
import com.chatroom.net.ChatMessage;
import com.chatroom.net.MessageType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog collecting the server address, port, desired username and
 * room before attempting a connection. On success it hands the live
 * {@link ChatClient} over to a new {@link ClientWindow} and closes itself.
 */
public class LoginDialog extends JDialog implements LanguageManager.LanguageChangeListener {

    private final JTextField hostField = new JTextField("localhost");
    private final JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(5000, 1, 65535, 1));
    private final JTextField usernameField = new JTextField();
    private final JTextField roomField = new JTextField(com.chatroom.net.ChatServer.DEFAULT_ROOM);
    private final JButton connectButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JLabel statusLabel = new JLabel(" ");

    private final JLabel hostLabel = new JLabel();
    private final JLabel portLabel = new JLabel();
    private final JLabel usernameLabel = new JLabel();
    private final JLabel roomLabel = new JLabel();

    private boolean connected = false;

    public LoginDialog(Frame owner) {
        super(owner, true);
        LanguageManager.getInstance().addListener(this);

        initComponents();
        applyLabels();
        applyOrientation(LanguageManager.getInstance().getCurrentLanguage());

        setSize(420, 320);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 10));
        form.add(fieldRow(hostLabel, hostField));
        form.add(fieldRow(portLabel, portSpinner));
        form.add(fieldRow(usernameLabel, usernameField));
        form.add(fieldRow(roomLabel, roomField));
        content.add(form, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(4, 4));
        statusLabel.setForeground(Color.RED);
        southPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        connectButton.addActionListener(e -> onConnect());
        cancelButton.addActionListener(e -> dispose());
        buttonRow.add(connectButton);
        buttonRow.add(cancelButton);
        southPanel.add(buttonRow, BorderLayout.SOUTH);

        content.add(southPanel, BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JPanel fieldRow(JLabel label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(4, 2));
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void onConnect() {
        LanguageManager lm = LanguageManager.getInstance();
        String host = hostField.getText().trim();
        int port = (Integer) portSpinner.getValue();
        String username = usernameField.getText().trim();
        String room = roomField.getText().trim();

        if (host.isEmpty() || username.isEmpty() || room.isEmpty()) {
            statusLabel.setText(lm.t("login.error.empty"));
            return;
        }

        connectButton.setEnabled(false);
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText(lm.t("client.status.connecting"));

        ChatClient client = new ChatClient();
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            private volatile ChatMessage joinResult;

            @Override
            protected Boolean doInBackground() {
                try {
                    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                    client.setListener(new ChatClient.ClientListener() {
                        @Override
                        public void onMessage(ChatMessage message) {
                            if (message.getType() == MessageType.JOIN_ACCEPTED
                                    || message.getType() == MessageType.JOIN_REJECTED) {
                                joinResult = message;
                                latch.countDown();
                            }
                        }

                        @Override
                        public void onDisconnected(String reason) {
                            latch.countDown();
                        }
                    });
                    client.connect(host, port, username, room);
                    latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
                    return joinResult != null && joinResult.getType() == MessageType.JOIN_ACCEPTED;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                connectButton.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        connected = true;
                        ClientWindow window = new ClientWindow(client, username, room);
                        window.setVisible(true);
                        dispose();
                    } else if (joinResult != null && joinResult.getType() == MessageType.JOIN_REJECTED) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText(lm.t("login.error.usernameTaken"));
                        client.disconnect();
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText(lm.t("login.error.connect"));
                        client.disconnect();
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(lm.t("login.error.connect"));
                }
            }
        };
        worker.execute();
    }

    public boolean isConnected() {
        return connected;
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.t("login.title"));
        hostLabel.setText(lm.t("login.host"));
        portLabel.setText(lm.t("login.port"));
        usernameLabel.setText(lm.t("login.username"));
        roomLabel.setText(lm.t("login.room"));
        connectButton.setText(lm.t("login.connect"));
        cancelButton.setText(lm.t("login.cancel"));
    }

    private void applyOrientation(Language language) {
        ComponentOrientation orientation = language.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;
        applyComponentOrientation(orientation);
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void onLanguageChanged(Language newLanguage) {
        applyLabels();
        applyOrientation(newLanguage);
    }
}
