package view;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class Attendance extends JFrame {

    private JTextField idField, dateField;
    private JTextField checkInField, checkOutField;
    private JComboBox<String> statusBox;
    private DefaultTableModel tableModel;

    public Attendance() {

        setTitle("Employee Management System - Attendance");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        idField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        checkInField = new JTextField();
        checkOutField = new JTextField();

        statusBox = new JComboBox<>(new String[]{
            "Present", "Absent", "Leave"
        });

        JButton saveButton = new JButton("Save Attendance");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(new JLabel("Employee ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Attendance Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Check-in Time (HH:MM):"));
        formPanel.add(checkInField);

        formPanel.add(new JLabel("Check-out Time (HH:MM):"));
        formPanel.add(checkOutField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);

        formPanel.add(saveButton);
        formPanel.add(refreshButton);

        String[] columns = {
            "Attendance ID",
            "Employee ID",
            "Date",
            "Check-in",
            "Check-out",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(
                new JScrollPane(attendanceTable),
                BorderLayout.CENTER
        );

        add(mainPanel);

        saveButton.addActionListener(e -> saveAttendance());
        refreshButton.addActionListener(e -> loadAttendance());

        loadAttendance();
    }

    private void saveAttendance() {

        String id = idField.getText().trim();
        String dateText = dateField.getText().trim();
        String inText = checkInField.getText().trim();
        String outText = checkOutField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (id.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter Employee ID and Date."
            );
            return;
        }

        try {

            LocalDate date = LocalDate.parse(dateText);

            LocalTime checkIn = inText.isEmpty()
                    ? null : LocalTime.parse(inText);

            LocalTime checkOut = outText.isEmpty()
                    ? null : LocalTime.parse(outText);

            if ("Present".equals(status)
                    && (checkIn == null || checkOut == null)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter check-in and check-out times."
                );
                return;
            }

            if (checkIn != null && checkOut != null
                    && !checkOut.isAfter(checkIn)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Check-out time must be after check-in time."
                );
                return;
            }

            String sql = "INSERT INTO Attendance "
                    + "(EmployeeID, AttendanceDate, "
                    + "CheckInTime, CheckOutTime, Status) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, id);
                stmt.setDate(2, java.sql.Date.valueOf(date));

                if (checkIn == null) {
                    stmt.setNull(3, Types.TIME);
                } else {
                    stmt.setTime(3, Time.valueOf(checkIn));
                }

                if (checkOut == null) {
                    stmt.setNull(4, Types.TIME);
                } else {
                    stmt.setTime(4, Time.valueOf(checkOut));
                }

                stmt.setString(5, status);

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(
                        this, "Attendance Saved Successfully!"
                );

                idField.setText("");
                checkInField.setText("");
                checkOutField.setText("");

                loadAttendance();
            }

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date or time format. "
                    + "Use YYYY-MM-DD and HH:MM."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }

    private void loadAttendance() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM Attendance "
                + "ORDER BY AttendanceDate DESC, AttendanceID DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("AttendanceID"),
                    rs.getString("EmployeeID"),
                    rs.getDate("AttendanceDate"),
                    rs.getTime("CheckInTime"),
                    rs.getTime("CheckOutTime"),
                    rs.getString("Status")
                };

                tableModel.addRow(row);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }
}
