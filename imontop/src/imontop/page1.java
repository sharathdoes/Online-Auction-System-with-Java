package imontop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

 class page1 {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/demoo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Update with your MySQL password

    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater() for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to Nimbus for a modern appearance
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Login or Register");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new BorderLayout());

            JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField();
            loginPanel.add(usernameLabel);
            loginPanel.add(usernameField);
            loginPanel.add(passwordLabel);
            loginPanel.add(passwordField);

            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            // Set a background color for buttons
            loginButton.setBackground(new Color(34, 139, 34)); // Forest Green
            registerButton.setBackground(new Color(100, 149, 237)); // Dodger Blue

            // Set text color for buttons
            loginButton.setForeground(Color.WHITE);
            registerButton.setForeground(Color.WHITE);

            // Set a font for buttons
            Font buttonFont = new Font("Arial", Font.PLAIN, 14);
            loginButton.setFont(buttonFont);
            registerButton.setFont(buttonFont);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, username);
                        statement.setString(2, password);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                JOptionPane.showMessageDialog(null, "Login successful");

                                // Open AuctionPage upon successful login
                                SwingUtilities.invokeLater(() -> {
                                    PageTwo auctionPage = new PageTwo();
                                    auctionPage.setVisible(true);

                                    // Close the login frame
                                    frame.dispose();
                                });
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid username or password");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            });


            // Register button action
            registerButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, username);
                        statement.setString(2, password);
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(null, "Registration successful");
                        } else {
                            JOptionPane.showMessageDialog(null, "Registration failed");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            });

            // Add components to the frame
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonsPanel.add(loginButton);
            buttonsPanel.add(registerButton);

            frame.add(loginPanel, BorderLayout.CENTER);
            frame.add(buttonsPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}