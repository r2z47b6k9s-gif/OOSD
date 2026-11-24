package view;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Departments extends JFrame {

    private JTextField idField, nameField, managerField;
    private JTextArea descriptionArea;
    private DefaultTableModel tableModel;

    public Departments() {

        setTitle("Employee Management System - Departments");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        idField = new JTextField();
        nameField = new JTextField();
        managerField = new JTextField();
        descriptionArea = new JTextArea(3, 20);

        JButton addButton = new JButton("Add Department");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(new JLabel("Department ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Department Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Manager Name:"));
        formPanel.add(managerField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        formPanel.add(addButton);
        formPanel.add(refreshButton);

        String[] columns = {
            "Department ID",
            "Department Name",
            "Manager Name",
            "Description"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable departmentTable = new JTable(tableModel);
        departmentTable.setRowHeight(25);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(
                new JScrollPane(departmentTable),
                BorderLayout.CENTER
        );

        add(mainPanel);

        addButton.addActionListener(e -> addDepartment());
        refreshButton.addActionListener(e -> loadDepartments());

        loadDepartments();
    }

    private void addDepartment() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String manager = managerField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Department ID and Department Name."
            );
            return;
        }

        String sql = "INSERT INTO Departments "
                + "(DepartmentID, DepartmentName, ManagerName, Description) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, manager);
            stmt.setString(4, description);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(
                    this, "Department Added Successfully!"
            );

            idField.setText("");
            nameField.setText("");
            managerField.setText("");
            descriptionArea.setText("");

            loadDepartments();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }

    private void loadDepartments() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM Departments ORDER BY DepartmentID";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                    rs.getString("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getString("ManagerName"),
                    rs.getString("Description")
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
