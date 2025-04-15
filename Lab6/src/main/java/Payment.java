import java.time.LocalDate;
import java.util.ArrayDeque;

public class Payment {

    public static boolean payUtilityBill(User user, Checking checking, UtilityCompany company) {
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

            System.out.println("Payment of $" + amountDue + " was successful.");
            System.out.println("Due Date: " + LocalDate.now().plusDays(30));
            return true;
        } else {
            System.out.println("Payment failed: Withdrawal limit reached or other issue.");
            return false;
        }
    }
}

