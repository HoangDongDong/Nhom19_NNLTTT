package com.edulanguage.UI;

import com.edulanguage.EduLanguageApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

/**
 * Điểm chạy ứng dụng desktop (Swing).
 * Không khởi động web server, chỉ mở cửa sổ đăng nhập.
 * Chạy: Run As -> Java Application trên lớp này.
 */
public class EduLanguageDesktopApplication {

    public static void main(String[] args) {
        // Bắt buộc: tắt headless để Swing/AWT chạy được (màn hình desktop)
        System.setProperty("java.awt.headless", "false");

        SpringApplication app = new SpringApplication(EduLanguageApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        ConfigurableApplicationContext context = app.run(args);

        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            LoginFrame loginFrame = new LoginFrame(context);
            loginFrame.setVisible(true);
        });
    }
}
