package view;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LeaveManagement extends JFrame {

    private JTextField idField, startField, endField;
    private JTextArea reasonArea;
    private JComboBox<String> leaveTypeBox;
    private DefaultTableModel tableModel;
    private JTable leaveTable;

    public LeaveManagement() {

        setTitle("Employee Management System - Leave Management");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        idField = new JTextField();
        startField = new JTextField();
        endField = new JTextField();
        reasonArea = new JTextArea(3, 20);

        leaveTypeBox = new JComboBox<>(new String[]{
            "Annual Leave", "Sick Leave", "Casual Leave"
        });

        formPanel.add(new JLabel("Employee ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Leave Type:"));
        formPanel.add(leaveTypeBox);

        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        formPanel.add(startField);

        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        formPanel.add(endField);

        formPanel.add(new JLabel("Reason:"));
        formPanel.add(new JScrollPane(reasonArea));

        String[] columns = {
            "Leave ID", "Employee ID", "Leave Type",
            "Start Date", "End Date", "Reason", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaveTable = new JTable(tableModel);
        leaveTable.setRowHeight(25);
        leaveTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton submitButton = new JButton("Submit Leave");
        JButton approveButton = new JButton("Approve Leave");
        JButton rejectButton = new JButton("Reject Leave");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(submitButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(
                new JScrollPane(leaveTable),
                BorderLayout.CENTER
        );
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        submitButton.addActionListener(e -> submitLeave());
        approveButton.addActionListener(e -> updateLeaveStatus("Approved"));
        rejectButton.addActionListener(e -> updateLeaveStatus("Rejected"));
        refreshButton.addActionListener(e -> loadLeaves());

        loadLeaves();
    }

    private void submitLeave() {

        String id = idField.getText().trim();
        String reason = reasonArea.getText().trim();
        String leaveType = leaveTypeBox.getSelectedItem().toString();

        if (id.isEmpty() || startField.getText().trim().isEmpty()
                || endField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all required fields."
            );
            return;
        }

        try {

            LocalDate startDate = LocalDate.parse(
                    startField.getText().trim()
            );

            LocalDate endDate = LocalDate.parse(
                    endField.getText().trim()
            );

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(
                        this, "End date cannot be before start date."
                );
                return;
            }

            String sql = "INSERT INTO LeaveRequests "
                    + "(EmployeeID, LeaveType, StartDate, "
                    + "EndDate, Reason, Status) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, id);
                stmt.setString(2, leaveType);
                stmt.setDate(3, java.sql.Date.valueOf(startDate));
                stmt.setDate(4, java.sql.Date.valueOf(endDate));
                stmt.setString(5, reason);
                stmt.setString(6, "Pending");

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(
                        this, "Leave Request Submitted Successfully!"
                );

                idField.setText("");
                startField.setText("");
                endField.setText("");
                reasonArea.setText("");

                loadLeaves();
            }

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this, "Invalid date format. Use YYYY-MM-DD."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }

    private void updateLeaveStatus(String status) {

        int selectedRow = leaveTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a leave request first."
            );
            return;
        }

        int modelRow = leaveTable.convertRowIndexToModel(selectedRow);
        int leaveID = (int) tableModel.getValueAt(modelRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to mark this leave as " + status + "?",
                "Confirm Leave Status",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE LeaveRequests SET Status = ? "
                + "WHERE LeaveID = ? AND Status = 'Pending'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, leaveID);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this, "Leave Request " + status + " Successfully!"
                );
            } else {
                JOptionPane.showMessageDialog(
                        this, "Only pending leave requests can be updated."
                );
            }

            loadLeaves();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }

    private void loadLeaves() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM LeaveRequests ORDER BY LeaveID DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("LeaveID"),
                    rs.getString("EmployeeID"),
                    rs.getString("LeaveType"),
                    rs.getDate("StartDate"),
                    rs.getDate("EndDate"),
                    rs.getString("Reason"),
                    rs.getString("Status")
                };

                tableModel.addRow(row);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }
}
