import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class UtilityCompanyUI extends JFrame {

    private static UtilityCompany company = new UtilityCompany();
    private User loggedInUser = null;

    public UtilityCompanyUI() {
        setTitle("Utility Company Portal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        getContentPane().removeAll();
        setLayout(new FlowLayout());

        JButton createAccountBtn = new JButton("Create Account");
        JButton loginBtn = new JButton("Login");

        createAccountBtn.addActionListener(e -> showCreateAccountScreen());
        loginBtn.addActionListener(e -> showLoginScreen());

        add(new JLabel("Welcome to the Utility Company"));
        add(createAccountBtn);
        add(loginBtn);

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

            // Check username uniqueness
            boolean exists = UtilityCompany.users.stream()
                    .anyMatch(u -> u.getUsername().equals(uname));
            if (exists) {
                JOptionPane.showMessageDialog(this, "Username already exists. Try another.");
                return;
            }

            user.setUsername(uname);
            user.setPassword(pass);
            user.setAccNum(rand.nextInt(1000000));
            UtilityCompany.users.add(user);

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
        JTextField passwordField = new JPasswordField();

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

            for (User u : UtilityCompany.users) {
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
        setLayout(new GridLayout(4, 1));

        JButton paymentHistoryBtn = new JButton("View Payment History");
        JButton nextPaymentBtn = new JButton("View Next Payment");
        JButton logoutBtn = new JButton("Logout");

        add(new JLabel("Welcome, " + loggedInUser.getUsername() + "!"));
        add(paymentHistoryBtn);
        add(nextPaymentBtn);
        add(logoutBtn);

        paymentHistoryBtn.addActionListener(e -> {
            ArrayDeque<Integer> history = loggedInUser.getPaymentHistoy();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payment history yet.");
            } else {
                JOptionPane.showMessageDialog(this, "Last Payments: " + history);
            }
        });

        nextPaymentBtn.addActionListener(e -> {
            int nextPayment = loggedInUser.getNextPayment();
            JOptionPane.showMessageDialog(this, "Next Payment: $" + nextPayment + "\nDue Date: 30 days from now");
        });

        logoutBtn.addActionListener(e -> {
            loggedInUser = null;
            showWelcomeScreen();
        });

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UtilityCompanyUI().setVisible(true));
    }
}
