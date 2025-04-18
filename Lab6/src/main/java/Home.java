import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {
    //This class is just a UI to have the UtilityCompany or ATM UI displayed.
    private User loggedInUser;
    public Home() {
        setTitle("Banking & Utility Hub");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(3, 1, 10, 10));

        JButton atmButton = new JButton("Go to ATM");
        JButton utilityButton = new JButton("Go to Utility Company");
        JButton nextDayButton = new JButton("Simulate Next Day");

        atmButton.addActionListener(e -> {
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(this, "Please log in through the Utility Company first.");
                return;
            }

            this.setVisible(false); // Hide Home window
            SwingUtilities.invokeLater(() -> {
                ATMInterface atm = new ATMInterface(this, loggedInUser);
                atm.setVisible(true);
            });
        });

        utilityButton.addActionListener(e -> {
            this.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                UtilityCompanyUI utilityUI = new UtilityCompanyUI(this);
                utilityUI.setVisible(true);
            });
        });

        nextDayButton.addActionListener(e -> {
            DayTracker.nextDay();  // You must have this implemented somewhere
            JOptionPane.showMessageDialog(this, "Day advanced! Current day: " + DayTracker.getCurrentDay());
        });

        add(atmButton);
        add(utilityButton);
        add(nextDayButton);
    }
    public void setLoggedInUser(User user){
        this.loggedInUser = user;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Home home = new Home();
            home.setVisible(true);
        });
    }
}