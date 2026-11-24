package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteEmployee extends JFrame {

    private JTextField idField;
    private JTextArea detailsArea;
    private JButton deleteButton;
    private boolean employeeFound = false;

    public DeleteEmployee() {

        setTitle("Employee Management System - Delete Employee");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel searchPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        idField = new JTextField();
        JButton searchButton = new JButton("Search Employee");

        searchPanel.add(new JLabel("Employee ID:"));
        searchPanel.add(idField);
        searchPanel.add(searchButton);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 15));

        deleteButton = new JButton("Delete Employee");
        deleteButton.setEnabled(false);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(deleteButton, BorderLayout.SOUTH);

        add(panel);

        searchButton.addActionListener(e -> searchEmployee());

        deleteButton.addActionListener(e -> deleteEmployee());

        idField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

            public void insertUpdate(
                    javax.swing.event.DocumentEvent e) {
                resetSearch();
            }

            public void removeUpdate(
                    javax.swing.event.DocumentEvent e) {
                resetSearch();
            }

            public void changedUpdate(
                    javax.swing.event.DocumentEvent e) {
                resetSearch();
            }
        }
        );
    }

    private void resetSearch() {
        employeeFound = false;
        deleteButton.setEnabled(false);
        detailsArea.setText("");
    }

    private void searchEmployee() {

        resetSearch();

        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter an Employee ID."
            );
            return;
        }

        String sql = "SELECT * FROM Employees WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    detailsArea.setText(
                            "Employee ID: " + rs.getString("EmployeeID")
                            + "\nFull Name: " + rs.getString("FullName")
                            + "\nEmail: " + rs.getString("Email")
                            + "\nPhone Number: " + rs.getString("PhoneNumber")
                            + "\nDepartment: " + rs.getString("Department")
                            + "\nGender: " + rs.getString("Gender")
                            + "\nSalary: " + rs.getBigDecimal("Salary")
                    );

                    employeeFound = true;
                    deleteButton.setEnabled(true);

                } else {

                    JOptionPane.showMessageDialog(
                            this, "Employee not found."
                    );
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }

    private void deleteEmployee() {

        if (!employeeFound) {
            JOptionPane.showMessageDialog(
                    this, "Please search for an employee first."
            );
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this employee?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Employees WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idField.getText().trim());

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {

                JOptionPane.showMessageDialog(
                        this, "Employee Deleted Successfully!"
                );

                idField.setText("");
                resetSearch();

            } else {

                JOptionPane.showMessageDialog(
                        this, "Employee not found."
                );

                resetSearch();
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }
}
