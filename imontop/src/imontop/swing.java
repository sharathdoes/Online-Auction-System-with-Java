package imontop;

import javax.swing.JButton;
import javax.swing.JFrame;

public class swing {
    public static void main(String[] args) {
        // Create a JFrame (window)
        JFrame frame = new JFrame("My Swing App");

        // Create a JButton
        JButton button = new JButton("Click me");

        // Add the button to the JFrame
        frame.getContentPane().add(button);

        // Set default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set size and make the frame visible
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}
