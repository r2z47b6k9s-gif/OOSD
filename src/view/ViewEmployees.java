
package view;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewEmployees extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public ViewEmployees() {

        setTitle("Employee Management System - View Employees");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {
            "Employee ID",
            "Full Name",
            "Email",
            "Phone Number",
            "Department",
            "Gender",
            "Salary"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JButton refreshButton = new JButton("Refresh");

        refreshButton.addActionListener(e -> loadEmployees());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEmployees();
    }

    private void loadEmployees() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM Employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getString("EmployeeID"),
                    rs.getString("FullName"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Department"),
                    rs.getString("Gender"),
                    rs.getBigDecimal("Salary")
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
