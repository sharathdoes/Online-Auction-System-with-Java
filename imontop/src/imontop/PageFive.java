package imontop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PageFive extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private String item;
    private int auctionEndTime;  // Auction end time in 24-hour format

    private DefaultTableModel tableModel;
    private JTable bidTable;

    private JLabel bidInfoLabel;
    private JTextField priceInput;
    private JButton submitButton;
    private JButton bidAgainButton;
    private JLabel highestBidLabel;

    public PageFive(String username, String item, int auctionEndTime) {
        this.username = username;
        this.item = item;
        this.auctionEndTime = auctionEndTime;

        setTitle("Bid Information");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create table model with columns
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Username");
        tableModel.addColumn("Item");
        tableModel.addColumn("Bid Price");

        // Create table with the model
        bidTable = new JTable(tableModel);

        // Set up scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(bidTable);

        bidInfoLabel = new JLabel("Enter Bid Price:");
        priceInput = new JTextField();
        highestBidLabel = new JLabel("Highest Bid Price: $0.0"); // Initialize with a default value

        // Increase the size of the text field
        priceInput.setPreferredSize(new Dimension(150, priceInput.getPreferredSize().height));

        submitButton = new JButton("Submit");
        bidAgainButton = new JButton("Bid Again");

        // Create a panel for bid input, buttons, and highest bid label
        JPanel bidPanel = new JPanel(new FlowLayout());
        bidPanel.add(bidInfoLabel);
        bidPanel.add(priceInput);
        bidPanel.add(submitButton);
        bidPanel.add(bidAgainButton);

        // Create a panel for the highest bid label
        JPanel highestBidPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        highestBidPanel.add(highestBidLabel);

        // Add components to the frame
        add(scrollPane, BorderLayout.CENTER);
        add(bidPanel, BorderLayout.SOUTH);
        add(highestBidPanel, BorderLayout.NORTH);

        // Fetch and display bid information from the database
        fetchBidInformation();
        updateHighestBid(); // Update the highest bid information initially

        // Add action listeners
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBidSubmission();
            }
        });

        bidAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                priceInput.setText("");
            }
        });
    }

    private void fetchBidInformation() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demoo","root","")) {
            String sql = "SELECT username, item, bid_price FROM bids";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Clear existing rows in the tableModel
                    tableModel.setRowCount(0);

                    // Iterate through the result set and add rows to the tableModel
                    while (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String item = resultSet.getString("item");
                        double bidPrice = resultSet.getDouble("bid_price");

                        Object[] bidData = {username, item, bidPrice};
                        tableModel.addRow(bidData);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void storeBidInDatabase(String username, String item, double bidPrice) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demoo","root","")) {
            String sql = "INSERT INTO bids (username, item, bid_price) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, item);
                statement.setDouble(3, bidPrice);
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Bid information stored in the database");
                } else {
                    System.out.println("Failed to store bid information");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void updateHighestBid() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demoo","root","")) {
            String sql = "SELECT MAX(bid_price) AS highest_bid FROM bids";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        double highestBid = resultSet.getDouble("highest_bid");
                        highestBidLabel.setText("Highest Bid Price: $" + highestBid);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void handleBidSubmission() {
        String bidPrice = priceInput.getText();
        if (!bidPrice.isEmpty()) {
            storeBidInDatabase(username, item, Double.parseDouble(bidPrice));
            fetchBidInformation(); // Refresh the bid information after submission
            updateHighestBid(); // Update the highest bid information
            priceInput.setText("");
            JOptionPane.showMessageDialog(null, "Bid submitted successfully!");

            // Check if auction time is over
            if (isAuctionTimeOver()) {
                handleAuctionCompletion();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a bid price!");
        }
    }

    private boolean isAuctionTimeOver() {
        int currentHour = java.time.LocalTime.now().getHour();
        return currentHour >= auctionEndTime;
    }

    private void handleAuctionCompletion() {
        // Fetch the highest bidder and price
        String[] highestBidInfo = getHighestBidderInfo();
        String highestBidder = highestBidInfo[0];
        String highestBidAmount = highestBidInfo[1];

        // Create a new JFrame for PageSix
        JFrame pageSixFrame = new JFrame("Page Six");
        pageSixFrame.setSize(400, 300);
        pageSixFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create components for PageSix
        JLabel congratsLabel = new JLabel("Congratulations!");
        JLabel winnerLabel = new JLabel("Winner: " + highestBidder);
        JLabel bidAmountLabel = new JLabel("Bid Amount: $" + highestBidAmount);
        JLabel deliveryMsgLabel = new JLabel("Your item will be delivered within 24 hours.");

        // Set layout for PageSix
        pageSixFrame.setLayout(new BorderLayout());

        // Add components to PageSix
        pageSixFrame.add(congratsLabel, BorderLayout.NORTH);
        pageSixFrame.add(winnerLabel, BorderLayout.CENTER);
        pageSixFrame.add(bidAmountLabel, BorderLayout.SOUTH);
        pageSixFrame.add(deliveryMsgLabel, BorderLayout.EAST);

        // Make PageSix visible
        pageSixFrame.setVisible(true);

        // Dispose of the current PageFive frame
        dispose();
    }

    private String[] getHighestBidderInfo() {
        String[] result = new String[2];
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demoo","root","")){
            String sql = "SELECT username, bid_price FROM bids WHERE bid_price = (SELECT MAX(bid_price) FROM bids)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        result[0] = resultSet.getString("username");
                        result[1] = resultSet.getString("bid_price");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PageFive("SampleUser", "Item 1",16).setVisible(true));
        
    }
}
