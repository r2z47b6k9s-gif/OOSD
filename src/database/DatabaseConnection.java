
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {

        String url =
            "jdbc:sqlserver://localhost;"
            + "instanceName=SQLEXPRESS;"
            + "databaseName=EmployeeManagementDB;"
            + "integratedSecurity=true;"
            + "authenticationScheme=NativeAuthentication;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

        return DriverManager.getConnection(url);
    }

    public static void main(String[] args) {

        try (Connection conn = getConnection()) {

            System.out.println("Database Connected Successfully!");

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            System.out.println(e.getMessage());
        }
    }
}
