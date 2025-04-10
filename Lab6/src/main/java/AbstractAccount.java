public abstract class AbstractAccount {
    private int balance;
    private final int maxDeposit;
    private int currentDeposit;

    public AbstractAccount (int maxDeposit) {
      this.maxDeposit = maxDeposit;
    }

    public void reset() {
      currentDeposit = 0;
    }

    public void deposit(int amount) throws AntiMoneyLaunderingException {
      if(amount + currentDeposit > maxDeposit) {
        throw new AntiMoneyLaunderingException();
      }

      currentDeposit += amount;

      balance += amount;
    }


    public int getBalance() {
      return balance;
    }
}
