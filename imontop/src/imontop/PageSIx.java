package imontop;



import javax.swing.*;
import java.awt.*;

 class PageSix extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PageSix(String winner, double bidAmount) {
        setTitle("Congratulations!");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create components for PageSix
        JLabel congratsLabel = new JLabel("Congratulations!");
        JLabel winnerLabel = new JLabel("Winner: " + winner);
        JLabel bidAmountLabel = new JLabel("Bid Amount: $" + bidAmount);
        JLabel deliveryLabel = new JLabel("Your item will be delivered within 24 hours.");

        // Set layout for PageSix
        setLayout(new BorderLayout());

        // Add components to PageSix
        add(congratsLabel, BorderLayout.NORTH);
        add(winnerLabel, BorderLayout.CENTER);
        add(bidAmountLabel, BorderLayout.SOUTH);
        add(deliveryLabel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PageSix("SampleUser", 100.0).setVisible(true));
    }
}

