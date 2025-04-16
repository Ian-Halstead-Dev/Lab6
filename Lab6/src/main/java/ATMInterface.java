import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.Set;

public class ATMInterface extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private double balance = 1000.00; // Example balance
    private Checking checking = new Checking();
    private Saving saving = new Saving();
    static Set<User> users = UserDataStore.loadUsers();

    public ATMInterface() {
        // Frame settings
        setTitle("ATM System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add screens
        mainPanel.add(createWelcomePanel(), "welcome");
        mainPanel.add(createPinPanel(), "pin");
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createWithdrawPanel(), "withdraw");
        mainPanel.add(createDepositPanel(), "deposit");
        mainPanel.add(createBalancePanel(), "balance");

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome"); // start screen
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label = new JLabel("Welcome to the ATM", SwingConstants.CENTER);
        JButton nextButton = new JButton("Insert Card (Next)");

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.TOP);

        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "pin"));
        // nextButton.setPreferredSize(new Dimension(100,50));
        nextButton.setBounds(100, 200, 200, 40);

        panel.add(label);
        panel.add(nextButton);

        return panel;
    }

    private JPanel createPinPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Enter your PIN:", SwingConstants.CENTER);
        JPasswordField pinField = new JPasswordField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            String enteredPin = new String(pinField.getPassword());
            Optional<User> matchedUser = users.stream()
                    .filter(u -> u.getPin() == Integer.parseInt(enteredPin))
                    .findFirst();

            if (matchedUser.isPresent()) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + matchedUser.get().getUsername());
                // optionally store the logged-in user:
                // this.loggedInUser = matchedUser.get();
                cardLayout.show(mainPanel, "menu");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN. Try again.");
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(pinField, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton exitButton = new JButton("Exit");

        withdrawButton.addActionListener(e -> cardLayout.show(mainPanel, "withdraw"));
        depositButton.addActionListener(e -> cardLayout.show(mainPanel, "deposit"));
        balanceButton.addActionListener(e -> cardLayout.show(mainPanel, "balance"));
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(balanceButton);
        panel.add(exitButton);

        return panel;
    }

    private JPanel createWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Enter amount to withdraw:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();
        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                boolean success = checking.withdraw(amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Withdrawal successful! New balance: $" + checking.getBalance());
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Withdrawal failed. Limit exceeded or insufficient balance.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.");
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(amountField, BorderLayout.CENTER);
        panel.add(confirmButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Enter amount to deposit:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();
        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> {
            double amount = Double.parseDouble(amountField.getText());
            balance += amount;
            JOptionPane.showMessageDialog(this, "Deposit successful! New balance: $" + balance);
            cardLayout.show(mainPanel, "menu");
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(amountField, BorderLayout.CENTER);
        panel.add(confirmButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Your current balance is: $" + balance, SwingConstants.CENTER);
        JButton backButton = new JButton("Back to Menu");

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        panel.add(label, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMInterface atm = new ATMInterface();
            atm.setVisible(true);
        });
    }
}