import javax.swing.*;
import java.awt.*;
import java.util.*;
//This class implements the UI for the utility company for the user to see their payment history and what
//payment they have to make.
public class UtilityCompanyUI extends JFrame {
    private User loggedInUser = null;
    private Checking userCheckingAccount = new Checking();
    static Set<User> users = UserDataStore.loadUsers();
    static { PaymentDataStore.loadPaymentHistories(users); }
    static { PinDataStore.loadPins(users); }
    private Home home;
    public UtilityCompanyUI(Home home) {
        setTitle("Utility Company Portal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.home = home;
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Top label
        JLabel titleLabel = new JLabel("Welcome to the Utility Company", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Center buttons (Create & Login)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton createAccountBtn = new JButton("Create Account");
        JButton loginBtn = new JButton("Login");

        createAccountBtn.addActionListener(e -> showCreateAccountScreen());
        loginBtn.addActionListener(e -> showLoginScreen());

        centerPanel.add(createAccountBtn);
        centerPanel.add(loginBtn);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom "Back to Home" button
        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> {
            this.dispose();  // Close Utility window
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
            User user = new User();
            String uname = usernameField.getText();
            String pass = passwordField.getText();
            Random rand = new Random();

            boolean exists = users.stream()
                    .anyMatch(u -> u.getUsername().equals(uname));
            if (exists) {
                JOptionPane.showMessageDialog(this, "Username already exists. Try another.");
                return;
            }

            user.setUsername(uname);
            user.setPassword(pass);
            user.setAccNum(rand.nextInt(1000000));
            users.add(user);
            UserDataStore.saveUsers(users);
            PaymentDataStore.savePaymentHistories(users);


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

            for (User u : users) {
                if ((u.getUsername().equals(input) || Integer.toString(u.getAccNum()).equals(input)) && u.getPassword().equals(pass)) {
                    loggedInUser = u;
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + u.getUsername());
                    showAccountScreen();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
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
        JButton logoutBtn = new JButton("Logout");
        JButton setPinButton = new JButton("Set/Update PIN");

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
                JOptionPane.showMessageDialog(this,
                        "PIN is already set and cannot be changed.",
                        "PIN Locked",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String pinText = JOptionPane.showInputDialog(this, "Enter a 4-digit PIN for your Checking account:");
            if (pinText == null) return;

            if (!pinText.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "Invalid PIN. Must be exactly 4 digits.");
                return;
            }

            int pin = Integer.parseInt(pinText);

            try {
                checking.setPin(pin);
                PinDataStore.savePins(users);
                JOptionPane.showMessageDialog(this, "PIN successfully set!");
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        nextPaymentBtn.addActionListener(e -> {
            int nextPayment = loggedInUser.getNextPayment();
            userCheckingAccount = loggedInUser.getCheckingAcct();
            int confirm = JOptionPane.showConfirmDialog(this, "Next Payment: $" + nextPayment + "\n\nDo you want to pay this now?", "Pay Bill", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Payment.payUtilityBill(loggedInUser, userCheckingAccount, this);
            }
        });

        logoutBtn.addActionListener(e -> {
            loggedInUser = null;
            showWelcomeScreen();
        });

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Home home = new Home(); // create the home screen
            UtilityCompanyUI utilityUI = new UtilityCompanyUI(home); // pass it to Utility
            utilityUI.setVisible(true);
        });
    }
}