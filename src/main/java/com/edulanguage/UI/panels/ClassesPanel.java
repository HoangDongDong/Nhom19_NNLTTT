package com.edulanguage.UI.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel danh sách lớp học (placeholder). Có thể tích hợp JTable + ClazzService sau.
 */
public class ClassesPanel extends JPanel {

    public ClassesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Danh sách lớp học", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JLabel placeholder = new JLabel("Nội dung danh sách lớp học (sẽ tích hợp bảng dữ liệu)", SwingConstants.CENTER);
        placeholder.setForeground(Color.GRAY);
        add(placeholder, BorderLayout.CENTER);
    }
}
