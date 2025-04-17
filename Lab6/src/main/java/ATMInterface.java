import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.Set;

public class ATMInterface extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Checking checking = new Checking();
    private Saving saving = new Saving();
    static Set<User> users = UserDataStore.loadUsers();
    private User loggedInUser;
    private Home home;
    //This class implements the front end UI for the ATM.
    public ATMInterface(Home home) {
        // Frame settings
        this.home = home;
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
                loggedInUser = matchedUser.get();
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
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> {
            this.dispose();  // Close ATM window
            home.setVisible(true);  // Show Home window again
        });

        panel.add(homeButton);
        withdrawButton.addActionListener(e -> cardLayout.show(mainPanel, "withdraw"));
        depositButton.addActionListener(e -> cardLayout.show(mainPanel, "deposit"));
        balanceButton.addActionListener(e -> {
            mainPanel.add(createBalancePanel(), "balance"); // create it fresh with latest balances
            cardLayout.show(mainPanel, "balance");
        });
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(balanceButton);
        JButton transferButton = new JButton("Transfer Funds");
        transferButton.addActionListener(e -> {
            mainPanel.add(createTransferPanel(), "transfer");
            cardLayout.show(mainPanel, "transfer");
        });

        panel.add(transferButton); // Add this before balanceButton or wherever you want it
        panel.add(exitButton);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Transfer Amount:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();

        String[] accountOptions = {"Checking to Saving", "Saving to Checking"};
        JComboBox<String> directionSelector = new JComboBox<>(accountOptions);

        JButton confirmButton = new JButton("Confirm");
        JButton backButton = new JButton("Back to Menu");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                String direction = (String) directionSelector.getSelectedItem();
                checking = loggedInUser.getCheckingAcct();
                saving = loggedInUser.getSavingAcct();

                if ("Checking to Saving".equals(direction)) {
                    checking.transfer(checking, saving, amount);
                    JOptionPane.showMessageDialog(this, "Transfer successful!\n" +
                            "Checking: $" + checking.getBalance() +
                            "\nSaving: $" + saving.getBalance());
                } else if ("Saving to Checking".equals(direction)) {
                    saving.transfer(checking, amount); // calls Saving.transfer override
                    JOptionPane.showMessageDialog(this, "Transfer successful!\n" +
                            "Saving: $" + saving.getBalance() +
                            "\nChecking: $" + checking.getBalance());
                }
                UserDataStore.saveUsers(users);
                cardLayout.show(mainPanel, "menu");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            } catch (InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(this, "Transfer failed: Insufficient funds.");
            } catch (AntiMoneyLaunderingException ex) {
                JOptionPane.showMessageDialog(this, "Transfer exceeds daily limit.", "Limit Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        inputPanel.add(label);
        inputPanel.add(amountField);
        inputPanel.add(directionSelector);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
    //This method creates the panel to withdraw money from the checking account.
    private JPanel createWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Enter amount to withdraw:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();
        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                boolean success = loggedInUser.getCheckingAcct().withdraw(amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Withdrawal successful! New balance: $" + loggedInUser.getCheckingAcct().getBalance());
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
        UserDataStore.saveUsers(users);
        return panel;
    }
    //This method creates the panel to deposit money into the checking or savings account, chosen by a drop down.
    private JPanel createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Enter amount to deposit:", SwingConstants.CENTER);
        JTextField amountField = new JTextField();

        String[] accountOptions = {"Checking", "Saving"};
        JComboBox<String> accountSelector = new JComboBox<>(accountOptions);

        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                String selected = (String) accountSelector.getSelectedItem();
                checking = loggedInUser.getCheckingAcct();
                saving = loggedInUser.getSavingAcct();
                boolean success = false;

                if ("Checking".equals(selected)) {
                    success = checking.deposit(amount);
                } else if ("Saving".equals(selected)) {
                    success = saving.deposit(amount);
                }

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Deposit successful!\nChecking: $" + checking.getBalance() +
                                    "\nSaving: $" + saving.getBalance());
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Deposit failed. Daily limit may have been exceeded.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input. Please enter a valid number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (AntiMoneyLaunderingException ex) {
                throw new RuntimeException(ex);
            }
            UserDataStore.saveUsers(users);
        });

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        inputPanel.add(label);
        inputPanel.add(amountField);
        inputPanel.add(accountSelector);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(confirmButton, BorderLayout.SOUTH);

        return panel;
    }
    //This method creates the panel for the user to check the balance of their accounts.
    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        checking = loggedInUser.getCheckingAcct();
        saving = loggedInUser.getSavingAcct();
        // Create balance labels
        JLabel checkingLabel = new JLabel("Checking Account Balance: $" + checking.getBalance(), SwingConstants.CENTER);
        JLabel savingLabel = new JLabel("Saving Account Balance: $" + saving.getBalance(), SwingConstants.CENTER);

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        // Stack both labels in a sub-panel
        JPanel balanceInfoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        balanceInfoPanel.add(checkingLabel);
        balanceInfoPanel.add(savingLabel);

        panel.add(balanceInfoPanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Home home = new Home(); // create home screen
            ATMInterface atm = new ATMInterface(home); // pass it to ATM
            atm.setVisible(true);
        });
    }
}
