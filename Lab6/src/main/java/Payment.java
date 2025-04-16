import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayDeque;

public class Payment {

    public static boolean payUtilityBill(User user, Checking checking, Component parent) {
        // Get the next bill amount
        int amountDue = user.getNextPayment();

        // Check if sufficient balance is available
        if (checking.getBalance() < amountDue) {
            System.out.println("Payment failed: Insufficient balance in checking account.");
            return false;
        }

        // Attempt to withdraw the payment amount from checking account
        boolean withdrawn = checking.withdraw(amountDue);

        if (withdrawn) {
            // Add payment to user's utility payment history (limited to 3 entries)
            ArrayDeque<Integer> history = user.getPaymentHistoy();
            user.setNextPayment(-1);
            if (history.size() == 3) {
                history.removeLast();
            }
            history.addFirst(amountDue);
            user.setPaymentHistory(history);

            JOptionPane.showMessageDialog(parent,
                    "Payment of $" + amountDue + " was successful.\nNext due date: " +
                            LocalDate.now().plusDays(30));
            return true;
        } else {
            JOptionPane.showMessageDialog(parent,
                    "Payment failed: Withdrawal limit reached or other issue.",
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

