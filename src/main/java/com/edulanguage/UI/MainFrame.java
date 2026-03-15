package com.edulanguage.UI;

import com.edulanguage.UI.panels.*;

import com.edulanguage.service.BranchService;
import com.edulanguage.service.PlacementTestService;
import com.edulanguage.service.RoomService;
import com.edulanguage.service.StaffService;
import com.edulanguage.service.StudentService;
import com.edulanguage.service.FinanceService;

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
    private static final String CARD_BRANCHES = "branches";
    private static final String CARD_ROOMS = "rooms";
    private static final String CARD_STAFFS = "staffs";
    private static final String CARD_INVOICES = "invoices";

    private final ConfigurableApplicationContext context;
    private final String username;
    private final String role;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private JPanel placeholderBranches;
    private JPanel placeholderRooms;
    private JPanel placeholderStaffs;
    private JPanel placeholderInvoices;
    private JPanel placeholderStudents;

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
        placeholderStudents = createPlaceholderPanel("Học viên", CARD_STUDENTS);
        contentPanel.add(placeholderStudents, CARD_STUDENTS);
        contentPanel.add(new TeachersPanel(), CARD_TEACHERS);
        contentPanel.add(new CoursesPanel(), CARD_COURSES);
        contentPanel.add(new ClassesPanel(), CARD_CLASSES);
        if (role != null && role.contains("ADMIN")) {
            addAdminPanels();
        }

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(sidebar, BorderLayout.WEST);
        main.add(contentPanel, BorderLayout.CENTER);

        add(main);
    }

    /** Tạo các panel Hệ thống cho ADMIN. Dùng placeholder — không gọi Service/DB khi đăng nhập. */
    private void addAdminPanels() {
        placeholderBranches = createPlaceholderPanel("Chi nhánh", CARD_BRANCHES);
        placeholderRooms = createPlaceholderPanel("Phòng học", CARD_ROOMS);
        placeholderStaffs = createPlaceholderPanel("Nhân viên", CARD_STAFFS);
        placeholderInvoices = createPlaceholderPanel("Tài chính", CARD_INVOICES);
        contentPanel.add(placeholderBranches, CARD_BRANCHES);
        contentPanel.add(placeholderRooms, CARD_ROOMS);
        contentPanel.add(placeholderStaffs, CARD_STAFFS);
        contentPanel.add(placeholderInvoices, CARD_INVOICES);
    }

    private JPanel createPlaceholderPanel(String title, String cardName) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        p.add(lbl, BorderLayout.NORTH);
        p.add(new JLabel("Nhấn '" + title + "' ở menu bên trái để mở", SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
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
        sidebar.add(createLazyMenuButton("Học viên", CARD_STUDENTS));
        sidebar.add(createMenuButton("Giáo viên", CARD_TEACHERS));
        sidebar.add(createMenuButton("Khóa học", CARD_COURSES));
        sidebar.add(createMenuButton("Lớp học", CARD_CLASSES));

        if (role != null && role.contains("ADMIN")) {
            sidebar.add(Box.createVerticalStrut(10));
            JLabel sysLabel = new JLabel("Hệ thống");
            sysLabel.setFont(sysLabel.getFont().deriveFont(Font.BOLD, 12f));
            sysLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sysLabel.setForeground(new Color(80, 80, 80));
            sidebar.add(sysLabel);
            sidebar.add(createAdminMenuButton("Chi nhánh", CARD_BRANCHES));
            sidebar.add(createAdminMenuButton("Phòng học", CARD_ROOMS));
            sidebar.add(createAdminMenuButton("Nhân viên", CARD_STAFFS));
            sidebar.add(createAdminMenuButton("Tài chính / Hóa đơn", CARD_INVOICES));
        }

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

    /** Nút menu lazy-load: khi click sẽ tải panel thật (nếu còn placeholder) rồi hiển thị. */
    private JButton createLazyMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setBackground(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            ensurePanelLoaded(cardName);
            cardLayout.show(contentPanel, cardName);
        });
        return btn;
    }

    /** Nút menu ADMIN: khi click sẽ tải panel CRUD (nếu còn placeholder) rồi hiển thị. */
    private JButton createAdminMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setBackground(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            ensureAdminPanelLoaded(cardName);
            cardLayout.show(contentPanel, cardName);
        });
        return btn;
    }

    /** Tải panel thật thay placeholder khi user click menu (lazy load). */
    private void ensurePanelLoaded(String cardName) {
        try {
            if (CARD_STUDENTS.equals(cardName) && placeholderStudents != null) {
                contentPanel.remove(placeholderStudents);
                contentPanel.add(new StudentsPanel(
                        context.getBean(StudentService.class),
                        context.getBean(PlacementTestService.class),
                        context.getBean(com.edulanguage.service.EnrollmentService.class),
                        context.getBean(com.edulanguage.dao.ClazzDao.class)), CARD_STUDENTS);
                placeholderStudents = null;
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Tải panel CRUD thật thay placeholder khi user click menu ADMIN (lazy load). */
    private void ensureAdminPanelLoaded(String cardName) {
        try {
            if (CARD_BRANCHES.equals(cardName) && placeholderBranches != null) {
                contentPanel.remove(placeholderBranches);
                contentPanel.add(new BranchesPanel(context.getBean(BranchService.class)), CARD_BRANCHES);
                placeholderBranches = null;
            } else if (CARD_ROOMS.equals(cardName) && placeholderRooms != null) {
                contentPanel.remove(placeholderRooms);
                contentPanel.add(new RoomsPanel(context.getBean(RoomService.class)), CARD_ROOMS);
                placeholderRooms = null;
            } else if (CARD_STAFFS.equals(cardName) && placeholderStaffs != null) {
                contentPanel.remove(placeholderStaffs);
                contentPanel.add(new StaffsPanel(context.getBean(StaffService.class)), CARD_STAFFS);
                placeholderStaffs = null;
            } else if (CARD_INVOICES.equals(cardName) && placeholderInvoices != null) {
                contentPanel.remove(placeholderInvoices);
                contentPanel.add(new InvoicesPanel(context.getBean(FinanceService.class)), CARD_INVOICES);
                placeholderInvoices = null;
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage()
                    + "\n\n(Ví dụ: chạy sql/create_branches.sql nếu bảng branches chưa có)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doLogout() {
        SecurityContextHolder.clearContext();
        dispose();
        LoginFrame loginFrame = new LoginFrame(context);
        loginFrame.setVisible(true);
    }
}
