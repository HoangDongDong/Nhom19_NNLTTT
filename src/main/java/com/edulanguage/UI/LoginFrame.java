package com.edulanguage.UI;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình đăng nhập desktop (Swing).
 * Sau khi đăng nhập thành công, mở MainFrame và đóng frame này.
 */
public class LoginFrame extends JFrame {

    private final ConfigurableApplicationContext context;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(ConfigurableApplicationContext context) {
        this.context = context;
        setTitle("Đăng nhập - Trung tâm Ngoại ngữ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridy = 0;
        center.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy = 1;
        usernameField = new JTextField(20);
        usernameField.setText("admin");
        center.add(usernameField, gbc);

        gbc.gridy = 2;
        center.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridy = 3;
        passwordField = new JPasswordField(20);
        passwordField.setText("123");
        center.add(passwordField, gbc);

        main.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> doLogin());
        south.add(btnLogin);
        main.add(south, BorderLayout.SOUTH);

        JLabel title = new JLabel("Hệ thống quản lý Trung tâm Ngoại ngữ", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        main.add(title, BorderLayout.NORTH);

        add(main);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ tên đăng nhập và mật khẩu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            AuthenticationManager authManager = context.getBean(AuthenticationManager.class);
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            dispose();
            MainFrame mainFrame = new MainFrame(context);
            mainFrame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng.",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
