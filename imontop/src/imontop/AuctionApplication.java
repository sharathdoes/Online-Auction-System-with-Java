package imontop;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import java.util.Timer;  // Add this import statement
import java.util.TimerTask;  

public class AuctionApplication {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/demoo";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PageTwo().setVisible(true));
    }
}

 class PageTwo extends JFrame {

    private JButton auctionButton;
    private JLabel countdownLabel;
    private Timer timer;
    private int countdown;

    private String[] items = {"Item 1", "Item 2", "Item 3"};
    private String[] itemDescriptions = {
            "Auction Item 1: A rare collectible that holds historical significance. Own a piece of the past with this extraordinary artifact.",
            "Auction Item 2: A classic timepiece known for its precision and elegance. This watch seamlessly combines style and functionality.",
            "Auction Item 3: An exquisite piece of artwork that captures the essence of modern aesthetics. This unique creation is a blend of vibrant colors and intricate details, making it a captivating addition to any art connoisseur's collection."
    };
    private int[] startTimes = {12, 21, 00}; // Auction start times in 24-hour format

    public PageTwo() {
        // Set up the frame
        setTitle("Auction Page");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        auctionButton = new JButton("Start Auction");
        countdownLabel = new JLabel("Time left: ");

        // Add action listener to the button
        auctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown > 0) {
                    navigateToAuctionPage(getCurrentAuctionItem());
                }
            }
        });

        // Set layout
        setLayout(new BorderLayout());

        // Add components to the frame
        add(auctionButton, BorderLayout.NORTH);
        add(countdownLabel, BorderLayout.CENTER);

        // Display item names and auction times using a JPanel with FlowLayout
        JPanel itemPanel = new JPanel(new FlowLayout());
        for (int i = 0; i < items.length; i++) {
            JLabel itemLabel = new JLabel(items[i] + " - " + startTimes[i] + ":00 to " + (startTimes[i] + 1) + ":00");
            itemPanel.add(itemLabel);
        }
        add(itemPanel, BorderLayout.SOUTH);

        // Enable or disable the "Start Auction" button based on the current time
        updateButtonAccessibility();

        // Start continuous countdown timer
        startContinuousCountdown();
    }

    private void updateButtonAccessibility() {
        int currentHour = java.time.LocalTime.now().getHour();

        for (int i = 0; i < startTimes.length; i++) {
            if (currentHour >= startTimes[i] && currentHour < startTimes[i] + 1) {
                auctionButton.setEnabled(true);
                countdown = (startTimes[i] + 1 - currentHour) * 3600 - java.time.LocalTime.now().getMinute() * 60 - java.time.LocalTime.now().getSecond(); // Countdown in seconds
                return;
            }
        }

        auctionButton.setEnabled(false);
        countdown = 0; // No countdown if not within auction time
    }

    private void startContinuousCountdown() {
        // Change the period to 60000 (60 seconds * 1000 milliseconds) for a 10-minute countdown
        Timer continuousCountdownTimer = new Timer();
        continuousCountdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateButtonAccessibility();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        countdownLabel.setText("Time left: " + formatTime(countdown));
                    }
                });

                if (countdown > 0) {
                    countdown--;

                    // Adjust the condition to check for 10 minutes
                    if (countdown == 600) {
                        countdown = 5; // Set to 5 seconds for testing purposes, change as needed
                    }
                }
            }
        }, 0, 1000); // Keep the initial delay as 0, and change the period to 1000 for 1-second intervals
    }


    private String getCurrentAuctionItem() {
        int currentHour = java.time.LocalTime.now().getHour();

        for (int i = 0; i < startTimes.length; i++) {
            if (currentHour >= startTimes[i] && currentHour < startTimes[i] + 1) {
                return items[i];
            }
        }

        return null; // Return null if not within auction time
    }

    private void navigateToAuctionPage(String currentAuctionItem) {
        if (currentAuctionItem != null) {
            // Create a new JFrame for PageThree
            JFrame pageThreeFrame = new JFrame("Page Three");
            pageThreeFrame.setSize(400, 300);
            pageThreeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Determine starting price based on the current auction item
            double startingPrice = getStartingPriceForItem(currentAuctionItem);

            // Create components for PageThree
            JLabel itemLabel = new JLabel("Auction Item: " + currentAuctionItem);
            JTextArea descriptionTextArea = new JTextArea(itemDescriptions[getIndexForItem(currentAuctionItem)]);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setLineWrap(true);
            descriptionTextArea.setEditable(false);

            JTextField priceInput = new JTextField();
            JButton placeBidButton = new JButton("Place Bid");

            // Display starting price on PageThree
            JLabel startingPriceLabel = new JLabel("Starting Price: $" + startingPrice);
            
            // Set layout for PageThree
            pageThreeFrame.setLayout(new BorderLayout());

            // Create a JPanel for bid input and button
            JPanel bidPanel = new JPanel(new FlowLayout());
            bidPanel.add(priceInput);
            bidPanel.add(placeBidButton);

            // Add components to PageThree
            pageThreeFrame.add(itemLabel, BorderLayout.NORTH);
            pageThreeFrame.add(descriptionTextArea, BorderLayout.CENTER);
            pageThreeFrame.add(startingPriceLabel, BorderLayout.WEST); // Display starting price label on the left
            pageThreeFrame.add(bidPanel, BorderLayout.SOUTH);

            // Add action listener to the "Place Bid" button
            placeBidButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String bidPrice = priceInput.getText();
                    // Handle bid placement logic here
                    System.out.println("Bid placed for " + currentAuctionItem + ": $" + bidPrice);
                }
            });

            // Make PageThree visible
            pageThreeFrame.setVisible(true);
        }
    }

    private int getIndexForItem(String item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private double getStartingPriceForItem(String item) {
        switch (item) {
            case "Item 1":
                return 78.0;
            case "Item 2":
                return 45.0;
            case "Item 3":
                return 65.0;
            default:
                return 0.0; // Default starting price if item is not recognized
        }
    }
 }


class PageThree extends JFrame {
    private String currentAuctionItem;
    private String[] items;
    private JLabel bidInfoLabel;
    private DefaultListModel<String> bidListModel;
    private JList<String> bidList;
    private JTextField priceInput;

    public PageThree(String currentAuctionItem, String[] items) {
        this.currentAuctionItem = currentAuctionItem;
        this.items = items;

        setTitle("Auction Item: " + currentAuctionItem);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel itemLabel = new JLabel("Auction Item: " + currentAuctionItem);
        JTextArea descriptionTextArea = new JTextArea(getItemDescription(currentAuctionItem));
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setEditable(false);

        bidInfoLabel = new JLabel("Bid Information:");

        bidListModel = new DefaultListModel<>();
        bidList = new JList<>(bidListModel);
        JScrollPane bidScrollPane = new JScrollPane(bidList);

        priceInput = new JTextField();
        JButton placeBidButton = new JButton("Place Bid");

        setLayout(new BorderLayout());

        JPanel bidPanel = new JPanel(new FlowLayout());
        bidPanel.add(priceInput);
        bidPanel.add(placeBidButton);

        add(itemLabel, BorderLayout.NORTH);
        add(descriptionTextArea, BorderLayout.CENTER);
        add(bidInfoLabel, BorderLayout.WEST);
        add(bidScrollPane, BorderLayout.CENTER);
        add(bidPanel, BorderLayout.SOUTH);

        placeBidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bidPrice = priceInput.getText();
                String username = "getUsernameFromYourLogic"; // Replace with actual logic

                System.out.println("Bid placed for " + currentAuctionItem + ": $" + bidPrice);

                storeBidInDatabase(username, currentAuctionItem, Double.parseDouble(bidPrice));

                bidListModel.addElement("Username: " + username + " | Bid Price: $" + bidPrice);

                priceInput.setText("");
            }
        });
    }

    private String getItemDescription(String item) {
        String[] itemDescriptions = {
                "Auction Item 1: A rare collectible that holds historical significance. Own a piece of the past with this extraordinary artifact.",
                "Auction Item 2: A classic timepiece known for its precision and elegance. This watch seamlessly combines style and functionality.",
                "Auction Item 3: An exquisite piece of artwork that captures the essence of modern aesthetics. This unique creation is a blend of vibrant colors and intricate details, making it a captivating addition to any art connoisseur's collection."
        };

        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                return itemDescriptions[i];
            }
        }
        return "";
    }

    private void storeBidInDatabase(String username, String item, double bidPrice) {
        try (Connection connection = DriverManager.getConnection(AuctionApplication.DB_URL, AuctionApplication.USERNAME, AuctionApplication.PASSWORD)) {
            String sql = "INSERT INTO bids (username, item, bid_price ) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, item);
                statement.setDouble(3, bidPrice);
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Bid information stored in the database");
                    // Optionally, display a message or update UI elements
                } else {
                    System.out.println("Failed to store bid information");
                    // Optionally, display an error message
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

}

class PageFour extends JFrame {
    private JLabel bidInfoLabel;

    public PageFour(String username, String item, String bidPrice) {
        setTitle("Bid Information");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        bidInfoLabel = new JLabel("Bid Information:");

        JLabel bidLabel = new JLabel("Username: " + username + " | Item: " + item + " | Bid Price: $" + bidPrice);

        setLayout(new BorderLayout());
        add(bidInfoLabel, BorderLayout.NORTH);
        add(bidLabel, BorderLayout.CENTER);
    }
}
 
