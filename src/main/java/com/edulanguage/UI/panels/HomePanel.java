package com.edulanguage.UI.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel trang chủ trong MainFrame (CardLayout).
 */
public class HomePanel extends JPanel {

    public HomePanel(String username, String role) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Chào mừng, " + username, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createTitledBorder("Thông tin đăng nhập"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        center.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        center.add(new JLabel(username), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        center.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(new Color(0, 100, 0));
        roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD));
        center.add(roleLabel, gbc);

        add(center, BorderLayout.CENTER);

        JTextArea info = new JTextArea(4, 40);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setFont(info.getFont().deriveFont(12f));
        info.setText("Bạn có quyền truy cập các chức năng theo vai trò " + role + ".\n\n"
                + "Hệ thống quản lý trung tâm ngoại ngữ — Project GK Nhóm 19.");
        add(new JScrollPane(info), BorderLayout.SOUTH);
    }
}
