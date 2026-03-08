package com.edulanguage.UI;

import com.edulanguage.UI.panels.*;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * Frame chính desktop: Sidebar Menu (trái) + CardLayout nội dung (phải).
 * Sau đăng nhập hiển thị theo vai trò; chuyển màn hình bằng menu không đổi cửa sổ.
 */
public class MainFrame extends JFrame {

    private static final String CARD_HOME = "home";
    private static final String CARD_STUDENTS = "students";
    private static final String CARD_TEACHERS = "teachers";
    private static final String CARD_COURSES = "courses";
    private static final String CARD_CLASSES = "classes";

    private final ConfigurableApplicationContext context;
    private final String username;
    private final String role;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    public MainFrame(ConfigurableApplicationContext context) {
        this.context = context;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        username = auth != null ? auth.getName() : "";
        role = auth != null && auth.getAuthorities() != null
                ? auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.replace("ROLE_", ""))
                    .collect(Collectors.joining(", "))
                : "";

        setTitle("Trung tâm Ngoại ngữ - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel sidebar = buildSidebar();
        contentPanel.add(new HomePanel(username, role), CARD_HOME);
        contentPanel.add(new StudentsPanel(), CARD_STUDENTS);
        contentPanel.add(new TeachersPanel(), CARD_TEACHERS);
        contentPanel.add(new CoursesPanel(), CARD_COURSES);
        contentPanel.add(new ClassesPanel(), CARD_CLASSES);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(sidebar, BorderLayout.WEST);
        main.add(contentPanel, BorderLayout.CENTER);

        add(main);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 245));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(15, 10, 15, 10)
        ));
        int sidebarWidth = 180;
        sidebar.setPreferredSize(new Dimension(sidebarWidth, 0));

        JLabel logo = new JLabel("Menu");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 14f));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 15, 0));
        sidebar.add(logo);

        sidebar.add(createMenuButton("Trang chủ", CARD_HOME));
        sidebar.add(createMenuButton("Học viên", CARD_STUDENTS));
        sidebar.add(createMenuButton("Giáo viên", CARD_TEACHERS));
        sidebar.add(createMenuButton("Khóa học", CARD_COURSES));
        sidebar.add(createMenuButton("Lớp học", CARD_CLASSES));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("Đăng xuất");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(sidebarWidth - 20, 36));
        logout.setBackground(new Color(200, 80, 80));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.addActionListener(e -> doLogout());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setBackground(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    private void doLogout() {
        SecurityContextHolder.clearContext();
        dispose();
        LoginFrame loginFrame = new LoginFrame(context);
        loginFrame.setVisible(true);
    }
}
