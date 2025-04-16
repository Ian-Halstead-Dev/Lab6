public abstract class AbstractAccount {
    protected int balance;
    private final int maxDeposit = 5000;
    private int currentDeposit;

    public AbstractAccount () {
        balance = 1000;
    }

    public void reset() {
      currentDeposit = 0;
    }

    public boolean deposit(int amount) throws AntiMoneyLaunderingException {
        if(amount < 0) {
            throw new IllegalArgumentException();
        }
      if(amount + currentDeposit > maxDeposit) {
        throw new AntiMoneyLaunderingException();
      }

      currentDeposit += amount;

      balance += amount;
      return true;
    }


    public int getBalance() {
      return balance;
    }
    public void setBalance(int balance) { this.balance = balance;}
}
