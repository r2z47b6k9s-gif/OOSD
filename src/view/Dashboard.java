package view;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("Employee Management System - Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel(
                "Employee Management System Dashboard",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        String[] options = {
            "Add Employee",
            "View Employees",
            "Update Employee",
            "Delete Employee",
            "Departments",
            "Attendance",
            "Leave Management",
            "Logout"
        };

        for (String option : options) {

            JButton button = new JButton(option);
            panel.add(button);

            button.addActionListener(e -> {

                if (option.equals("Logout")) {

                    new LoginPage().setVisible(true);
                    dispose();

                } else if (option.equals("Add Employee")) {

                    new AddEmployee().setVisible(true);

                } else if (option.equals("View Employees")) {

                    new ViewEmployees().setVisible(true);

                } else if (option.equals("Update Employee")) {

                    new UpdateEmployee().setVisible(true);

                } else if (option.equals("Delete Employee")) {

                    new DeleteEmployee().setVisible(true);

                } else if (option.equals("Departments")) {

                    new Departments().setVisible(true);

                } else if (option.equals("Attendance")) {

                    new Attendance().setVisible(true);

                } else if (option.equals("Leave Management")) {

                    new LeaveManagement().setVisible(true);

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            option + " page will be created soon."
                    );
                }
            });
        }

        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}
