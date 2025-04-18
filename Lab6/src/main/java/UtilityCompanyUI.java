import javax.swing.*;
import java.awt.*;
import java.util.*;

public class UtilityCompanyUI extends JFrame {
    private User loggedInUser = null;
    static Set<User> users = UserDataStore.loadUsers();

    static {
        PaymentDataStore.loadPaymentHistories(users);
        PinDataStore.loadPins(users);
    }

    private final Home home;

    public UtilityCompanyUI(Home home) {
        this.home = home;
        setTitle("Utility Company Portal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to the Utility Company", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton createAccountBtn = new JButton("Create Account");
        JButton loginBtn = new JButton("Login");

        createAccountBtn.addActionListener(e -> showCreateAccountScreen());
        loginBtn.addActionListener(e -> showLoginScreen());

        centerPanel.add(createAccountBtn);
        centerPanel.add(loginBtn);
        add(centerPanel, BorderLayout.CENTER);

        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> {
            this.dispose();
            home.setVisible(true);
        });
        add(homeButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showCreateAccountScreen() {
        getContentPane().removeAll();
        setLayout(new GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
        JButton createBtn = new JButton("Create");
        JButton backBtn = new JButton("Back");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(createBtn);
        add(backBtn);

        createBtn.addActionListener(e -> {
            String uname = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (uname.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
                return;
            }

            boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(uname));
            if (exists) {
                JOptionPane.showMessageDialog(this, "Username already exists. Try another.");
                return;
            }

            User user = new User();
            user.setUsername(uname);
            user.setPassword(pass);
            user.setAccNum(100000 + new Random().nextInt(900000));
            user.setNextPayment(50 + new Random().nextInt(100)); // Initial bill

            users.add(user);
            saveAll();

            JOptionPane.showMessageDialog(this, "Account created! Your account number: " + user.getAccNum());
            showWelcomeScreen();
        });

        backBtn.addActionListener(e -> showWelcomeScreen());
        revalidate();
        repaint();
    }

    private void showLoginScreen() {
        getContentPane().removeAll();
        setLayout(new GridLayout(4, 2));

        JTextField usernameOrAccField = new JTextField();
        JTextField passwordField = new JTextField();
        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        add(new JLabel("Username or Account Number:"));
        add(usernameOrAccField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginBtn);
        add(backBtn);

        loginBtn.addActionListener(e -> {
            String input = usernameOrAccField.getText();
            String pass = passwordField.getText();

            // First, find the matching user BEFORE loading
            User matchingUser = null;
            for (User u : users) {
                if ((u.getUsername().equals(input) || Integer.toString(u.getAccNum()).equals(input)) && u.getPassword().equals(pass)) {
                    matchingUser = u;
                    break;
                }
            }

            if (matchingUser != null) {
                // Reload all data fresh from disk
                users = UserDataStore.loadUsers();
                PaymentDataStore.loadPaymentHistories(users);
                PinDataStore.loadPins(users);

                // Find the reloaded version of the matched user (same accNum)
                for (User reloaded : users) {
                    if (reloaded.getAccNum() == matchingUser.getAccNum()) {
                        loggedInUser = reloaded;
                        break;
                    }
                }

                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + loggedInUser.getUsername());


                home.setLoggedInUser(loggedInUser);

                showAccountScreen();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
            }
        });

        backBtn.addActionListener(e -> showWelcomeScreen());
        revalidate();
        repaint();
    }

    private void showAccountScreen() {
        getContentPane().removeAll();
        setLayout(new GridLayout(5, 1));

        JButton paymentHistoryBtn = new JButton("View Payment History");
        JButton nextPaymentBtn = new JButton("View Next Payment");
        JButton setPinButton = new JButton("Set/Update PIN");
        JButton logoutBtn = new JButton("Logout");

        add(new JLabel("Welcome, " + loggedInUser.getUsername() + "!"));
        add(paymentHistoryBtn);
        add(nextPaymentBtn);
        add(setPinButton);
        add(logoutBtn);

        paymentHistoryBtn.addActionListener(e -> {
            ArrayDeque<Integer> history = loggedInUser.getPaymentHistoy();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payment history yet.");
            } else {
                JOptionPane.showMessageDialog(this, "Last Payments: " + history);
            }
        });

        setPinButton.addActionListener(e -> {
            Checking checking = loggedInUser.getCheckingAcct();

            if (checking.hasPin()) {
                JOptionPane.showMessageDialog(this, "PIN is already set and cannot be changed.",
                        "PIN Locked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String pinText = JOptionPane.showInputDialog(this, "Enter a 4-digit PIN for your Checking account:");
            if (pinText == null || !pinText.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "Invalid PIN. Must be exactly 4 digits.");
                return;
            }

            try {
                checking.setPin(Integer.parseInt(pinText));
                PinDataStore.savePins(users);
                UserDataStore.saveUsers(users);
                JOptionPane.showMessageDialog(this, "PIN successfully set!");
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        nextPaymentBtn.addActionListener(e -> {
            int nextPayment = loggedInUser.getNextPayment();
            if (nextPayment <= 0) {
                JOptionPane.showMessageDialog(this, "No payment due right now.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Next Payment: $" + nextPayment + "\n\nDo you want to pay this now?",
                    "Pay Bill", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean paid = Payment.payUtilityBill(loggedInUser, loggedInUser.getCheckingAcct(), this);

                if (paid) {
                    // Optional: generate next bill here instead of setting -1
                    // loggedInUser.setNextPayment(50 + new Random().nextInt(100));

                    saveAll();
                    JOptionPane.showMessageDialog(this, "Payment successful. Thank you!");
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            saveAll();
            loggedInUser = null;
            showWelcomeScreen();
        });

        revalidate();
        repaint();
    }

    private void saveAll() {
        UserDataStore.saveUsers(users);
        PaymentDataStore.savePaymentHistories(users);
        PinDataStore.savePins(users);
    }

    private void reloadData() {
        users = UserDataStore.loadUsers();
        PaymentDataStore.loadPaymentHistories(users);
        PinDataStore.loadPins(users);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Home home = new Home();
            UtilityCompanyUI utilityUI = new UtilityCompanyUI(home);
            utilityUI.setVisible(true);
        });
    }
}