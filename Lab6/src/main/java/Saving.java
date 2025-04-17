// Saving Account:
// • The user can have one saving account.
// • The user can deposit up to $5000 per day using one or more transactions.
// • The user can NOT withdraw any money from this account.
// • The user can transfer up to $100 per day from their saving account to their checking account
// using one or more transactions.
// • The user can NOT use this account to pay bills.
// • The user can check the balance of their saving account.
// • The bank does not allow overdraft, the balance can NOT be negative.
//This class implements the account for savings
public class Saving extends AbstractAccount{
  int maxTransfer;
  private int transferToday = 0;
  private int lastTransferDay = -1;

  public Saving() {
    super(); // Withdraw here is used as
    this.maxTransfer = 100;
  }

  public void transfer(AbstractAccount toAccount, int amount ) throws InsufficientFundsException, AntiMoneyLaunderingException {
    int today = DayTracker.getCurrentDay();

    // Reset daily tracker if new day
    if (today != lastTransferDay) {
      transferToday = 0;
      lastTransferDay = today;
    }

    if(amount + transferToday > 100) {

      throw new AntiMoneyLaunderingException();
    }

    super.transfer(this, toAccount, amount);
  }

}
