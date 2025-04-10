// Checking Account:
// • The user can have one checking account.
// • The user can deposit up to $5000 per day to this account using one or more transactions.
// • The user can withdraw a maximum of $500 per day using one or more transactions.
// • The user can transfer any amount of money from their checking account to their saving account.
// • The user can use this checking account to pay bills to their utility company.
// • The user can check the balance of their checking account.
// • The bank does not allow overdraft, the balance can NOT be negative


public class Checking extends AbstractAccount {
    public Checking() {
        super(5000, 500);
    }

    
}