package com.chatroom.gui.client.components;

import com.chatroom.i18n.LanguageManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sidebar showing every user currently present in the active room.
 * Double-clicking a name triggers the supplied callback so the owning
 * window can open a private-message prompt.
 */
public class UserListPanel extends JPanel {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private final TitledBorder border;

    public UserListPanel(Consumer<String> onDoubleClickUser) {
        setLayout(new BorderLayout());
        LanguageManager lm = LanguageManager.getInstance();
        border = BorderFactory.createTitledBorder(lm.t("client.onlineUsers"));
        setBorder(border);

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = list.getSelectedValue();
                    if (selected != null) {
                        onDoubleClickUser.accept(selected);
                    }
                }
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
        setPreferredSize(new Dimension(180, 0));
    }

    public void setUsers(List<String> users) {
        listModel.clear();
        for (String u : users) {
            listModel.addElement(u);
        }
    }

    public void refreshLabel() {
        LanguageManager lm = LanguageManager.getInstance();
        border.setTitle(lm.t("client.onlineUsers"));
        repaint();
    }
}
