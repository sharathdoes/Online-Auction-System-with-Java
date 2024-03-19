package imontop;


import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // This import is important for using java.sql.Statement


public class topam {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/demoo";
        String user = "root";
        String password = "";

        // Establish a connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");
            Statement st = connection.createStatement();
            String sql ="SELECT * FROM  TASTE ";
//            st.executeUpdate(sql);

            // Perform database operations here
            ResultSet rs= st.executeQuery(sql);
            while(rs.next()) {
            	System.out.print(rs.getInt(1));
            	System.out.print(rs.getInt(2));

            }

        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
}
