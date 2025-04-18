import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.Set;

public class ATMInterface extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Checking checking;
    private Saving saving;
    private final User loggedInUser;
    private final Home home;

    // ✅ Constructor now accepts loggedInUser directly
    public ATMInterface(Home home, User loggedInUser) {
        this.home = home;
        this.loggedInUser = loggedInUser;
        this.checking = loggedInUser.getCheckingAcct();
        this.saving = loggedInUser.getSavingAcct();

        setTitle("ATM System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomePanel(), "welcome");
        mainPanel.add(createPinPanel(), "pin");
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createWithdrawPanel(), "withdraw");
        mainPanel.add(createDepositPanel(), "deposit");

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(null);

        JLabel label = new JLabel("Welcome to the ATM", SwingConstants.CENTER);
        JButton nextButton = new JButton("Insert Card (Next)");

        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "pin"));
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
            try {
                int enteredPin = Integer.parseInt(new String(pinField.getPassword()));
                if (checking.verifyPin(enteredPin)) {
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + loggedInUser.getUsername());
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid PIN. Try again.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid PIN format.");
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(pinField, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton transferButton = new JButton("Transfer Funds");
        JButton homeButton = new JButton("Back to Home");

        withdrawButton.addActionListener(e -> cardLayout.show(mainPanel, "withdraw"));
        depositButton.addActionListener(e -> cardLayout.show(mainPanel, "deposit"));
        balanceButton.addActionListener(e -> {
            mainPanel.add(createBalancePanel(), "balance");
            cardLayout.show(mainPanel, "balance");
        });
        transferButton.addActionListener(e -> {
            mainPanel.add(createTransferPanel(), "transfer");
            cardLayout.show(mainPanel, "transfer");
        });
        homeButton.addActionListener(e -> {
            // Save before going back
            UserDataStore.saveUsers(Set.of(loggedInUser));
            PaymentDataStore.savePaymentHistories(Set.of(loggedInUser));
            this.dispose();
            home.setVisible(true);
        });

        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(balanceButton);
        panel.add(transferButton);
        panel.add(homeButton);

        return panel;
    }

    private JPanel createWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Enter amount to withdraw:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();
        JButton confirmButton = new JButton("Confirm");
        JButton backButton = new JButton("Back to Menu");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                boolean success = checking.withdraw(amount);
                if (success) {
                    UserDataStore.saveUsers(Set.of(loggedInUser));
                    JOptionPane.showMessageDialog(this, "Withdrawal successful! New balance: $" + checking.getBalance());
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds or withdrawal limit exceeded.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JPanel bottom = new JPanel();
        bottom.add(confirmButton);
        bottom.add(backButton);

        panel.add(label, BorderLayout.NORTH);
        panel.add(amountField, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Enter amount to deposit:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();

        String[] options = {"Checking", "Saving"};
        JComboBox<String> accountSelector = new JComboBox<>(options);

        JButton confirmButton = new JButton("Confirm");
        JButton backButton = new JButton("Back to Menu");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                boolean success = false;
                if ("Checking".equals(accountSelector.getSelectedItem())) {
                    success = checking.deposit(amount);
                } else if ("Saving".equals(accountSelector.getSelectedItem())) {
                    success = saving.deposit(amount);
                }

                if (success) {
                    UserDataStore.saveUsers(Set.of(loggedInUser));
                    JOptionPane.showMessageDialog(this,
                            "Deposit successful!\nChecking: $" + checking.getBalance() +
                                    "\nSaving: $" + saving.getBalance());
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Deposit failed. Daily limit may have been exceeded.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        inputPanel.add(label);
        inputPanel.add(amountField);
        inputPanel.add(accountSelector);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Transfer Amount:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();

        String[] directions = {"Checking to Saving", "Saving to Checking"};
        JComboBox<String> directionSelector = new JComboBox<>(directions);

        JButton confirmButton = new JButton("Confirm");
        JButton backButton = new JButton("Back to Menu");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                if ("Checking to Saving".equals(directionSelector.getSelectedItem())) {
                    checking.transfer(checking, saving, amount);
                } else {
                    saving.transfer(checking, amount);
                }

                UserDataStore.saveUsers(Set.of(loggedInUser));
                JOptionPane.showMessageDialog(this, "Transfer successful!");
                cardLayout.show(mainPanel, "menu");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Transfer failed: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        inputPanel.add(label);
        inputPanel.add(amountField);
        inputPanel.add(directionSelector);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel checkingLabel = new JLabel("Checking: $" + checking.getBalance(), SwingConstants.CENTER);
        JLabel savingLabel = new JLabel("Saving: $" + saving.getBalance(), SwingConstants.CENTER);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(checkingLabel);
        center.add(savingLabel);

        panel.add(center, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    // Don't use this — use UtilityCompanyUI to launch with a real User
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Use UtilityCompanyUI to start the app.");
        });
    }
}