package com.edulanguage.UI.panels;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Course;
import com.edulanguage.entity.Room;
import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.Schedule;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.CourseService;
import com.edulanguage.service.RoomService;
import com.edulanguage.service.ScheduleService;
import com.edulanguage.service.TeacherService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel danh sách lớp học.
 * - Với TEACHER: chỉ hiển thị các lớp mà giáo viên đó dạy.
 * - Với role khác: hiển thị toàn bộ lớp.
 */
public class ClassesPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final String role;
    private final Long teacherId;
    private final ConfigurableApplicationContext context;

    public ClassesPanel(ConfigurableApplicationContext context, String role, Long teacherId) {
        this.context = context;
        this.role = role;
        this.teacherId = teacherId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Lớp học", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giáo viên", "Phòng", "Bắt đầu", "Kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData(context.getBean(ClazzDao.class), role, teacherId);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Chỉ ADMIN mới được mở lớp & xếp lịch & đổi trạng thái
        boolean isAdmin = role != null && role.contains("ADMIN");
        if (isAdmin) {
            JButton btnAddClass = new JButton("Thêm lớp");
            btnAddClass.addActionListener(e -> openCreateClassDialog());
            JButton btnSchedule = new JButton("Xếp lịch");
            btnSchedule.addActionListener(e -> openScheduleDialog());
            JButton btnChangeStatus = new JButton("Đổi trạng thái");
            btnChangeStatus.addActionListener(e -> changeStatusSelectedClass());
            south.add(btnAddClass);
            south.add(btnSchedule);
            south.add(btnChangeStatus);
        }

        JButton btnViewStudents = new JButton("Xem học viên trong lớp");
        btnViewStudents.addActionListener(e -> showStudentsOfSelectedClass(context.getBean(ClazzDao.class)));
        south.add(btnViewStudents);

        add(south, BorderLayout.SOUTH);
    }

    /** Đổi trạng thái lớp (ACTIVE, PENDING, COMPLETED, ...) cho lớp đang chọn. */
    private void changeStatusSelectedClass() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp để đổi trạng thái.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long clazzId = (Long) tableModel.getValueAt(row, 0);
        ClazzDao clazzDao = context.getBean(ClazzDao.class);
        Clazz clazz = clazzDao.findById(clazzId).orElse(null);
        if (clazz == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lớp.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Status current = clazz.getStatus();
        Status newStatus = (Status) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới cho lớp:",
                "Đổi trạng thái lớp",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Status.values(),
                current != null ? current : Status.ACTIVE
        );

        if (newStatus == null) return; // user cancel

        try {
            clazz.setStatus(newStatus);
            clazzDao.save(clazz);
            loadData(clazzDao, role, teacherId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Dialog thêm lớp học mới (Admin). */
    private void openCreateClassDialog() {
        CourseService courseService = context.getBean(CourseService.class);
        TeacherService teacherService = context.getBean(TeacherService.class);
        RoomService roomService = context.getBean(RoomService.class);
        ClazzDao clazzDao = context.getBean(ClazzDao.class);

        java.util.List<Course> courses = courseService.findAll();
        java.util.List<Teacher> teachers = teacherService.findAll();
        java.util.List<Room> rooms = roomService.findAll();

        if (courses.isEmpty() || teachers.isEmpty() || rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cần có sẵn Khóa học, Giáo viên và Phòng học trước khi mở lớp.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Mở lớp mới", true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tên lớp
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Tên lớp (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfName = new JTextField(25);
        p.add(tfName, gbc);
        row++;

        // Khóa học
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Khóa học (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Course> cbCourse = new JComboBox<>(courses.toArray(new Course[0]));
        cbCourse.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value != null ? value.getCourseName() : ""));
        p.add(cbCourse, gbc);
        row++;

        // Giáo viên
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Giáo viên (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Teacher> cbTeacher = new JComboBox<>(teachers.toArray(new Teacher[0]));
        cbTeacher.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value != null ? value.getFullName() : ""));
        p.add(cbTeacher, gbc);
        row++;

        // Phòng học
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Phòng học (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Room> cbRoom = new JComboBox<>(rooms.toArray(new Room[0]));
        cbRoom.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value != null ? value.getRoomName() : ""));
        p.add(cbRoom, gbc);
        row++;

        // Ngày bắt đầu / kết thúc
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Bắt đầu (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfStart = new JTextField(12);
        p.add(tfStart, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Kết thúc (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfEnd = new JTextField(12);
        p.add(tfEnd, gbc);
        row++;

        // Sĩ số tối đa
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Sĩ số tối đa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfMax = new JTextField(6);
        p.add(tfMax, gbc);
        row++;

        // Nút
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        buttons.add(btnSave);
        buttons.add(btnCancel);
        p.add(buttons, gbc);

        btnCancel.addActionListener(e -> dlg.dispose());

        btnSave.addActionListener(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập tên lớp.");
                return;
            }
            Course course = (Course) cbCourse.getSelectedItem();
            Teacher teacher = (Teacher) cbTeacher.getSelectedItem();
            Room room = (Room) cbRoom.getSelectedItem();
            if (course == null || teacher == null || room == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn đầy đủ Khóa học, Giáo viên, Phòng.");
                return;
            }
            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(tfStart.getText().trim());
                endDate = LocalDate.parse(tfEnd.getText().trim());
                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(dlg, "Ngày kết thúc phải sau ngày bắt đầu.");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Ngày bắt đầu/kết thúc không hợp lệ (yyyy-MM-dd).");
                return;
            }
            Integer maxStudent = null;
            if (!tfMax.getText().trim().isEmpty()) {
                try {
                    maxStudent = Integer.parseInt(tfMax.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dlg, "Sĩ số tối đa phải là số nguyên.");
                    return;
                }
            }

            Clazz clazz = new Clazz();
            clazz.setClassName(name);
            clazz.setCourse(course);
            clazz.setTeacher(teacher);
            clazz.setRoom(room);
            clazz.setStartDate(startDate);
            clazz.setEndDate(endDate);
            clazz.setMaxStudent(maxStudent);
            // Mặc định trạng thái ACTIVE để có thể ghi danh
            clazz.setStatus(Status.ACTIVE);

            try {
                clazzDao.save(clazz);
                dlg.dispose();
                loadData(clazzDao, role, teacherId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi khi lưu lớp: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.getContentPane().add(p);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /** Dialog xếp lịch tự động cho lớp đã chọn (Admin). */
    private void openScheduleDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp để xếp lịch.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long clazzId = (Long) tableModel.getValueAt(row, 0);
        ClazzDao clazzDao = context.getBean(ClazzDao.class);
        Clazz clazz = clazzDao.findById(clazzId).orElse(null);
        if (clazz == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lớp.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        RoomService roomService = context.getBean(RoomService.class);
        java.util.List<Room> rooms = roomService.findAll();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Xếp lịch cho lớp " + clazz.getClassName(), true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int rowIdx = 0;

        // Phòng dùng cho lịch (mặc định phòng của lớp)
        gbc.gridx = 0; gbc.gridy = rowIdx; gbc.weightx = 0;
        p.add(new JLabel("Phòng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Room> cbRoom = new JComboBox<>(rooms.toArray(new Room[0]));
        if (clazz.getRoom() != null) {
            cbRoom.setSelectedItem(clazz.getRoom());
        }
        cbRoom.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value != null ? value.getRoomName() : ""));
        p.add(cbRoom, gbc);
        rowIdx++;

        // Giờ bắt đầu / kết thúc
        gbc.gridx = 0; gbc.gridy = rowIdx; gbc.weightx = 0;
        p.add(new JLabel("Giờ bắt đầu (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfStartTime = new JTextField("08:00", 8);
        p.add(tfStartTime, gbc);
        rowIdx++;

        gbc.gridx = 0; gbc.gridy = rowIdx; gbc.weightx = 0;
        p.add(new JLabel("Giờ kết thúc (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfEndTime = new JTextField("10:00", 8);
        p.add(tfEndTime, gbc);
        rowIdx++;

        // Chọn thứ trong tuần
        gbc.gridx = 0; gbc.gridy = rowIdx; gbc.weightx = 0;
        p.add(new JLabel("Ngày học trong tuần:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JCheckBox cbMon = new JCheckBox("T2");
        JCheckBox cbTue = new JCheckBox("T3");
        JCheckBox cbWed = new JCheckBox("T4");
        JCheckBox cbThu = new JCheckBox("T5");
        JCheckBox cbFri = new JCheckBox("T6");
        JCheckBox cbSat = new JCheckBox("T7");
        JCheckBox cbSun = new JCheckBox("CN");
        daysPanel.add(cbMon); daysPanel.add(cbTue); daysPanel.add(cbWed);
        daysPanel.add(cbThu); daysPanel.add(cbFri); daysPanel.add(cbSat); daysPanel.add(cbSun);
        p.add(daysPanel, gbc);
        rowIdx++;

        // Nút
        gbc.gridx = 0; gbc.gridy = rowIdx; gbc.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGenerate = new JButton("Tự động xếp lịch");
        JButton btnClose = new JButton("Đóng");
        btns.add(btnGenerate);
        btns.add(btnClose);
        p.add(btns, gbc);

        btnClose.addActionListener(e -> dlg.dispose());

        btnGenerate.addActionListener(e -> {
            LocalTime start;
            LocalTime end;
            try {
                start = LocalTime.parse(tfStartTime.getText().trim());
                end = LocalTime.parse(tfEndTime.getText().trim());
                if (!end.isAfter(start)) {
                    JOptionPane.showMessageDialog(dlg, "Giờ kết thúc phải sau giờ bắt đầu.");
                    return;
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dlg, "Giờ không hợp lệ (HH:mm).");
                return;
            }

            java.util.List<Integer> days = new ArrayList<>();
            if (cbMon.isSelected()) days.add(1);
            if (cbTue.isSelected()) days.add(2);
            if (cbWed.isSelected()) days.add(3);
            if (cbThu.isSelected()) days.add(4);
            if (cbFri.isSelected()) days.add(5);
            if (cbSat.isSelected()) days.add(6);
            if (cbSun.isSelected()) days.add(7);
            if (days.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ít nhất một ngày học.");
                return;
            }

            Room room = (Room) cbRoom.getSelectedItem();
            if (room == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn phòng học.");
                return;
            }

            ScheduleService scheduleService = context.getBean(ScheduleService.class);
            try {
                scheduleService.autoGenerateSchedule(clazz, room, start, end, days);
                JOptionPane.showMessageDialog(dlg, "Đã xếp lịch cho lớp.", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi khi xếp lịch: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.getContentPane().add(p);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void loadData(ClazzDao clazzDao, String role, Long teacherId) {
        tableModel.setRowCount(0);
        boolean isTeacher = role != null && role.contains("TEACHER") && teacherId != null;

        List<Clazz> classes = isTeacher
                ? clazzDao.findByTeacherId(teacherId)
                : clazzDao.findAll();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Clazz c : classes) {
            Course course = c.getCourse();
            Teacher teacher = c.getTeacher();
            Room room = c.getRoom();
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getClassName(),
                    course != null ? course.getCourseName() : "",
                    teacher != null ? teacher.getFullName() : "",
                    room != null ? room.getRoomName() : "",
                    c.getStartDate() != null ? c.getStartDate().format(df) : "",
                    c.getEndDate() != null ? c.getEndDate().format(df) : "",
                    c.getStatus() != null ? c.getStatus().name() : ""
            });
        }
    }

    private void showStudentsOfSelectedClass(ClazzDao clazzDao) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long clazzId = (Long) tableModel.getValueAt(row, 0);
        Clazz clazz = clazzDao.findById(clazzId).orElse(null);
        if (clazz == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin lớp học.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.List<Enrollment> enrollments = clazz.getEnrollments();
        if (enrollments == null || enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp học hiện chưa có học viên.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"ID", "Họ tên", "Email", "Số điện thoại", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Enrollment e : enrollments) {
            Student s = e.getStudent();
            if (s == null) continue;
            model.addRow(new Object[]{
                    s.getId(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getStatus() != null ? s.getStatus().name() : ""
            });
        }

        JTable tbl = new JTable(model);
        tbl.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scroll,
                "Học viên của lớp: " + clazz.getClassName(),
                JOptionPane.PLAIN_MESSAGE);
    }
}
