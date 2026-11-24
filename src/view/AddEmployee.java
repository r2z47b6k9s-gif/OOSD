package view;

import java.awt.*;
import javax.swing.*;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddEmployee extends JFrame {

    private JTextField idField, nameField, emailField;
    private JTextField phoneField, salaryField;
    private JComboBox<String> departmentBox;
    private JRadioButton maleButton, femaleButton;

    public AddEmployee() {

        setTitle("Employee Management System - Add Employee");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        salaryField = new JTextField();

        String[] departments = {
            "IT", "HR", "Finance", "Marketing", "Sales"
        };

        departmentBox = new JComboBox<>(departments);

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        JButton saveButton = new JButton("Save Employee");
        JButton clearButton = new JButton("Clear");

        panel.add(new JLabel("Employee ID:"));
        panel.add(idField);

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

        panel.add(saveButton);
        panel.add(clearButton);

        saveButton.addActionListener(e -> {

            if (idField.getText().trim().isEmpty()
                    || nameField.getText().trim().isEmpty()
                    || emailField.getText().trim().isEmpty()
                    || phoneField.getText().trim().isEmpty()
                    || salaryField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this, "Please fill in all fields."
                );
                return;
            }

            String sql = "INSERT INTO Employees "
                    + "(EmployeeID, FullName, Email, PhoneNumber, "
                    + "Department, Gender, Salary) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                String gender = maleButton.isSelected() ? "Male"
                        : femaleButton.isSelected() ? "Female" : "";

                if (gender.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select a gender.");
                    return;
                }

                double salary = Double.parseDouble(salaryField.getText().trim());

                if (salary < 0) {
                    JOptionPane.showMessageDialog(this, "Salary cannot be negative.");
                    return;
                }

                stmt.setString(1, idField.getText().trim());
                stmt.setString(2, nameField.getText().trim());
                stmt.setString(3, emailField.getText().trim());
                stmt.setString(4, phoneField.getText().trim());
                stmt.setString(5, departmentBox.getSelectedItem().toString());
                stmt.setString(6, gender);
                stmt.setDouble(7, salary);

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Employee Added Successfully!");

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this, "Please enter a valid salary.");

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage());
            }

        });

        clearButton.addActionListener(e -> {

            idField.setText("");
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            salaryField.setText("");
            departmentBox.setSelectedIndex(0);
            genderGroup.clearSelection();
        });

        add(panel);
    }
}
