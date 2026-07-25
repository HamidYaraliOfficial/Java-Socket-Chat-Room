package com.chatroom.gui.client.components;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Scrollable, read-only transcript of the current room's conversation.
 * Kept intentionally simple (a styled {@link JTextPane}) so it renders
 * correctly under both LTR and RTL component orientations.
 */
public class ChatAreaPanel extends JPanel {

    private final JTextPane textPane = new JTextPane();
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public ChatAreaPanel() {
        setLayout(new BorderLayout());
        textPane.setEditable(false);
        textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendSystemLine(String text) {
        appendStyled("* " + text, Color.GRAY, true);
    }

    public void appendChatLine(String sender, String content, boolean self) {
        String time = java.time.LocalTime.now().format(timeFormat);
        Color color = self ? new Color(0x00, 0x67, 0xC0) : new Color(0x20, 0x20, 0x20);
        appendStyled("[" + time + "] " + sender + ": " + content, color, false);
    }

    public void appendPrivateLine(String label, String content) {
        appendStyled(label + " " + content, new Color(0x9C, 0x27, 0xB0), false);
    }

    public void appendServerNotice(String label, String content) {
        appendStyled(label + " " + content, new Color(0xC6, 0x28, 0x28), true);
    }

    private void appendStyled(String text, Color color, boolean italic) {
        SwingUtilities.invokeLater(() -> {
            javax.swing.text.StyledDocument doc = textPane.getStyledDocument();
            javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
            javax.swing.text.StyleConstants.setForeground(attrs, color);
            javax.swing.text.StyleConstants.setItalic(attrs, italic);
            try {
                doc.insertString(doc.getLength(), text + "\n", attrs);
            } catch (javax.swing.text.BadLocationException ignored) {
            }
            textPane.setCaretPosition(doc.getLength());
        });
    }

    public void clear() {
        textPane.setText("");
    }

    public String getPlainText() {
        return textPane.getText();
    }

    public void applyOrientation(ComponentOrientation orientation) {
        textPane.applyComponentOrientation(orientation);
    }
}
