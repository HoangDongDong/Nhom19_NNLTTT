package com.edulanguage.UI;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * Màn hình chính desktop sau khi đăng nhập. Hiển thị theo vai trò (ADMIN, TEACHER, STUDENT, STAFF).
 */
public class MainFrame extends JFrame {

    private final ConfigurableApplicationContext context;

    public MainFrame(ConfigurableApplicationContext context) {
        this.context = context;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "";
        String role = auth != null && auth.getAuthorities() != null
                ? auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.replace("ROLE_", ""))
                    .collect(Collectors.joining(", "))
                : "";

        setTitle("Trang chủ - Trung tâm Ngoại ngữ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);
        buildUI(username, role);
    }

    private void buildUI(String username, String role) {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Chào mừng, " + username, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createTitledBorder("Thông tin đăng nhập"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        center.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        center.add(new JLabel(username), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(new Color(0, 100, 0));
        roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD));
        center.add(roleLabel, gbc);

        main.add(center, BorderLayout.CENTER);

        JTextArea info = new JTextArea(4, 40);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setFont(info.getFont().deriveFont(12f));
        info.setText("Bạn có quyền truy cập các chức năng theo vai trò " + role + ".\n\n"
                + "Hệ thống quản lý trung tâm ngoại ngữ — Project GK Nhóm 19.");
        main.add(new JScrollPane(info), BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Tài khoản");
        JMenuItem logout = new JMenuItem("Đăng xuất");
        logout.addActionListener(e -> {
            SecurityContextHolder.clearContext();
            dispose();
            LoginFrame loginFrame = new LoginFrame(context);
            loginFrame.setVisible(true);
        });
        menu.add(logout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        add(main);
    }
}
