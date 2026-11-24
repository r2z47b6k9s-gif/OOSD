package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

public class UpdateEmployee extends JFrame {

    private JTextField idField, nameField, emailField;
    private JTextField phoneField, salaryField;
    private JComboBox<String> departmentBox;
    private JRadioButton maleButton, femaleButton;
    private ButtonGroup genderGroup;

    public UpdateEmployee() {

        setTitle("Employee Management System - Update Employee");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        salaryField = new JTextField();

        departmentBox = new JComboBox<>(new String[]{
            "IT", "HR", "Finance", "Marketing", "Operations"
        });

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");

        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        JButton searchButton = new JButton("Search Employee");
        JButton updateButton = new JButton("Update Employee");

        panel.add(new JLabel("Employee ID:"));
        panel.add(idField);

        panel.add(new JLabel(""));
        panel.add(searchButton);

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);

        panel.add(new JLabel("Department:"));
        panel.add(departmentBox);

        panel.add(new JLabel("Gender:"));
        panel.add(genderPanel);

        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);

        panel.add(new JLabel(""));
        panel.add(updateButton);

        add(panel);

        searchButton.addActionListener(e -> searchEmployee());
        updateButton.addActionListener(e -> updateEmployee());
    }

    private void searchEmployee() {

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

                    nameField.setText(rs.getString("FullName"));
                    emailField.setText(rs.getString("Email"));
                    phoneField.setText(rs.getString("PhoneNumber"));
                    departmentBox.setSelectedItem(
                            rs.getString("Department")
                    );

                    String gender = rs.getString("Gender");

                    if ("Male".equalsIgnoreCase(gender)) {
                        maleButton.setSelected(true);
                    } else if ("Female".equalsIgnoreCase(gender)) {
                        femaleButton.setSelected(true);
                    } else {
                        genderGroup.clearSelection();
                    }

                    BigDecimal salary = rs.getBigDecimal("Salary");
                    salaryField.setText(
                            salary == null ? "" : salary.toString()
                    );

                    JOptionPane.showMessageDialog(
                            this, "Employee Found!"
                    );

                } else {

                    clearDetails();

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

    private void updateEmployee() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (id.isEmpty() || name.isEmpty()
                || email.isEmpty() || salaryField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all required fields."
            );
            return;
        }

        String gender = maleButton.isSelected() ? "Male"
                : femaleButton.isSelected() ? "Female" : "";

        if (gender.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please select a gender."
            );
            return;
        }

        BigDecimal salary;

        try {
            salary = new BigDecimal(salaryField.getText().trim());

            if (salary.signum() < 0) {
                JOptionPane.showMessageDialog(
                        this, "Salary cannot be negative."
                );
                return;
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this, "Please enter a valid salary."
            );
            return;
        }

        String sql = "UPDATE Employees SET "
                + "FullName = ?, Email = ?, PhoneNumber = ?, "
                + "Department = ?, Gender = ?, Salary = ? "
                + "WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(
                    4, departmentBox.getSelectedItem().toString()
            );
            stmt.setString(5, gender);
            stmt.setBigDecimal(6, salary);
            stmt.setString(7, id);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {

                JOptionPane.showMessageDialog(
                        this, "Employee Updated Successfully!"
                );

            } else {

                JOptionPane.showMessageDialog(
                        this, "Employee not found."
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this, "Database Error: " + ex.getMessage()
            );
        }
    }

    private void clearDetails() {

        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        salaryField.setText("");
        departmentBox.setSelectedIndex(0);
        genderGroup.clearSelection();
    }
}
