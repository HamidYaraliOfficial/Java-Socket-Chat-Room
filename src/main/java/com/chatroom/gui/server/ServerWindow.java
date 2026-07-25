package com.chatroom.gui.server;

import com.chatroom.gui.Theme;
import com.chatroom.gui.ThemeManager;
import com.chatroom.i18n.Language;
import com.chatroom.i18n.LanguageManager;
import com.chatroom.net.ChatServer;
import com.chatroom.net.ConnectedClientInfo;
import com.chatroom.util.TextFileExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Administrative window for hosting a chat server: configure the port,
 * start/stop the listener, watch the live list of connected clients
 * (username, address, room, connection time), kick a selected client,
 * broadcast a server-wide announcement, and review the running log.
 */
public class ServerWindow extends JFrame implements LanguageManager.LanguageChangeListener,
        ThemeManager.ThemeChangeListener, ChatServer.ServerListener {

    private final ChatServer server = new ChatServer();
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu languageMenu;
    private JMenu themeMenu;
    private JMenu helpMenu;
    private JMenuItem saveLogItem;
    private JMenuItem exitItem;
    private JMenuItem aboutItem;

    private final JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(5000, 1, 65535, 1));
    private final JButton startStopButton = new JButton();
    private final JLabel statusLabel = new JLabel();

    private final ClientsTableModel tableModel = new ClientsTableModel();
    private final JTable clientsTable = new JTable(tableModel);
    private final JButton kickButton = new JButton();

    private final JTextField broadcastField = new JTextField();
    private final JButton broadcastButton = new JButton();

    private final JTextArea logArea = new JTextArea();

    private final JLabel portLabel = new JLabel();
    private final TitledBorder clientsBorder;
    private final TitledBorder logBorder;
    private final TitledBorder broadcastBorder;

    public ServerWindow() {
        LanguageManager.getInstance().addListener(this);
        ThemeManager.getInstance().addListener(this);
        server.setListener(this);

        LanguageManager lm = LanguageManager.getInstance();
        clientsBorder = BorderFactory.createTitledBorder(lm.t("server.connectedClients"));
        logBorder = BorderFactory.createTitledBorder(lm.t("server.log"));
        broadcastBorder = BorderFactory.createTitledBorder(lm.t("server.broadcast"));

        initComponents();
        applyLabels();
        applyOrientation(lm.getCurrentLanguage());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                server.stop();
                System.exit(0);
            }
        });
        setSize(900, 620);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(760, 480));
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 4));
        topBar.add(portLabel);
        topBar.add(portSpinner);
        startStopButton.addActionListener(e -> onStartStop());
        topBar.add(startStopButton);
        topBar.add(statusLabel);
        content.add(topBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        JPanel clientsPanel = new JPanel(new BorderLayout(6, 6));
        clientsPanel.setBorder(clientsBorder);
        clientsTable.setRowHeight(24);
        clientsPanel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);
        JPanel kickRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        kickButton.addActionListener(e -> onKick());
        kickRow.add(kickButton);
        clientsPanel.add(kickRow, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));

        JPanel broadcastPanel = new JPanel(new BorderLayout(6, 6));
        broadcastPanel.setBorder(broadcastBorder);
        broadcastPanel.add(broadcastField, BorderLayout.CENTER);
        broadcastButton.addActionListener(e -> onBroadcast());
        broadcastPanel.add(broadcastButton, BorderLayout.EAST);
        bottomPanel.add(broadcastPanel, BorderLayout.NORTH);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(logBorder);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        bottomPanel.add(logPanel, BorderLayout.CENTER);

        splitPane.setTopComponent(clientsPanel);
        splitPane.setBottomComponent(bottomPanel);
        content.add(splitPane, BorderLayout.CENTER);

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
        exitItem.addActionListener(e -> {
            server.stop();
            System.exit(0);
        });
        fileMenu.add(saveLogItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

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
        menuBar.add(languageMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);
    }

    private void onStartStop() {
        LanguageManager lm = LanguageManager.getInstance();
        if (server.isRunning()) {
            server.stop();
        } else {
            int port = (Integer) portSpinner.getValue();
            try {
                server.start(port);
                startStopButton.setText(lm.t("server.stop"));
                statusLabel.setText(lm.t("server.statusRunning") + " " + port);
                portSpinner.setEnabled(false);
            } catch (Exception ex) {
                appendLog("Error: " + ex.getMessage());
            }
        }
    }

    private void onKick() {
        int row = clientsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        LanguageManager lm = LanguageManager.getInstance();
        ConnectedClientInfo info = tableModel.getAt(clientsTable.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this, lm.t("dialog.confirmKick"),
                lm.t("dialog.confirmTitle"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            server.kick(info.getUsername());
        }
    }

    private void onBroadcast() {
        String text = broadcastField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        server.broadcastServerNotice(text);
        broadcastField.setText("");
    }

    private void onSaveLog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("server-log.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                new TextFileExporter().export(logArea.getText(), chooser.getSelectedFile().getAbsolutePath());
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

    private void appendLog(String line) {
        String time = java.time.LocalTime.now().format(timeFormat);
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + time + "] " + line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public void onLog(String line) {
        appendLog(line);
    }

    @Override
    public void onClientsChanged(List<ConnectedClientInfo> clients) {
        SwingUtilities.invokeLater(() -> tableModel.setData(clients));
    }

    @Override
    public void onServerStopped() {
        SwingUtilities.invokeLater(() -> {
            LanguageManager lm = LanguageManager.getInstance();
            startStopButton.setText(lm.t("server.start"));
            statusLabel.setText(lm.t("server.statusStopped"));
            portSpinner.setEnabled(true);
            tableModel.setData(new ArrayList<>());
        });
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.t("server.title"));
        portLabel.setText(lm.t("server.port"));
        startStopButton.setText(server.isRunning() ? lm.t("server.stop") : lm.t("server.start"));
        statusLabel.setText(server.isRunning() ? lm.t("server.statusRunning") + " " + server.getPort() : lm.t("server.statusStopped"));
        kickButton.setText(lm.t("server.kick"));
        broadcastButton.setText(lm.t("server.send"));
        clientsBorder.setTitle(lm.t("server.connectedClients"));
        logBorder.setTitle(lm.t("server.log"));
        broadcastBorder.setTitle(lm.t("server.broadcast"));

        fileMenu.setText(lm.t("menu.file"));
        saveLogItem.setText(lm.t("menu.file.saveLog"));
        exitItem.setText(lm.t("menu.file.exit"));
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

        tableModel.refreshColumns();
        repaint();
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

    private class ClientsTableModel extends AbstractTableModel {
        private List<ConnectedClientInfo> data = new ArrayList<>();
        private String[] columns;

        ClientsTableModel() {
            refreshColumns();
        }

        void refreshColumns() {
            LanguageManager lm = LanguageManager.getInstance();
            columns = new String[]{
                    lm.t("server.column.username"), lm.t("server.column.address"),
                    lm.t("server.column.room"), lm.t("server.column.connectedAt")
            };
            fireTableStructureChanged();
        }

        void setData(List<ConnectedClientInfo> data) {
            this.data = data;
            fireTableDataChanged();
        }

        ConnectedClientInfo getAt(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ConnectedClientInfo c = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return c.getUsername();
                case 1:
                    return c.getAddress();
                case 2:
                    return c.getRoom();
                case 3:
                    return c.getConnectedAt().format(timeFormat);
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
