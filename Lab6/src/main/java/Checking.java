// Checking Account:
// • The user can have one checking account.
// • The user can deposit up to $5000 per day to this account using one or more transactions.
// • The user can withdraw a maximum of $500 per day using one or more transactions.
// • The user can transfer any amount of money from their checking account to their saving account.
// • The user can use this checking account to pay bills to their utility company.
// • The user can check the balance of their checking account.
// • The bank does not allow overdraft, the balance can NOT be negative

//This is the class for a checking account
public class Checking extends AbstractAccount {

    private int withdrawToday = 0;
    private int lastWithdrawDay = -1;

    public Checking() {
        super();
    }


    public boolean withdraw(int amount) {

        if(amount <= 0) {
            throw new IllegalArgumentException();
        }
        int today = DayTracker.getCurrentDay();



        // Reset daily tracker if new day
        if (today != lastWithdrawDay) {
            withdrawToday = 0;
            lastWithdrawDay = today;
        }

        if (withdrawToday + amount > 500) {
            System.out.println("Daily withdrawal limit reached.");
            return false;
        }

        if (amount <= balance) {
            balance -= amount;
            withdrawToday += amount;
            return true;
        } else {
            System.out.println("Insufficient balance.");
            return false;
        }
    }

    
}