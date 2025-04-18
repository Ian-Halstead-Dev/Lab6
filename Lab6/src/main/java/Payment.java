import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayDeque;

public class Payment {
    //This is the class that allows the user to pay their utility bill.
    public static boolean payUtilityBill(User user, Checking checking, Component parent) {
        int amountDue = user.getNextPayment();

        if (checking.getBalance() < amountDue) {
            JOptionPane.showMessageDialog(parent,
                    "Payment failed: Insufficient balance in checking account.",
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean withdrawn = checking.withdraw(amountDue);  //  use passed-in checking account

        if (withdrawn) {
            // Update payment history
            ArrayDeque<Integer> history = user.getPaymentHistoy();
            if (history.size() == 3) {
                history.removeLast();  // maintain size limit
            }
            history.addFirst(amountDue);
            user.setPaymentHistory(history);

            user.setNextPayment(-1);  //  reset next payment

            // Save everything
            UserDataStore.saveUsers(UtilityCompanyUI.users);
            PaymentDataStore.savePaymentHistories(UtilityCompanyUI.users);

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

