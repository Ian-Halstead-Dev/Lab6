public abstract class AbstractAccount {
    protected int balance;
    private final int maxDeposit = 5000;
    private int withdrawToday = 0;
    private int lastWithdrawDay = -1;
    private int currentDeposit;
    //This class is used to implement account methods that will be used in checking and saving.
    public AbstractAccount () {
        balance = 1000;
    }

    public void reset() {
      currentDeposit = 0;
    }

    public boolean deposit(int amount) throws AntiMoneyLaunderingException {
        int today = DayTracker.getCurrentDay();
        //If deposit amount is negative.
        if(amount < 0) {
            throw new IllegalArgumentException();
        }
        // Reset daily tracker if new day
        if (today != lastWithdrawDay) {
            withdrawToday = 0;
            lastWithdrawDay = today;
        }
        //If deposit amount for the day plus the amount to be deposited is greater than the max deposit amount (5000).
        if(amount + currentDeposit > maxDeposit) {
            throw new AntiMoneyLaunderingException();
        }
        //Add amount to the balance and to the current amount deposited today.
        currentDeposit += amount;
        balance += amount;
        //Return successful deposit.
        return true;
    }


    public int getBalance() {
      return balance;
    }
    public void setBalance(int balance) { this.balance = balance;}
}
