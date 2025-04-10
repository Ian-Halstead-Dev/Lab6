// The Utility Company
// • The user will create a new account at the utility company using a username and a password.
// • The utility company will give the user a new account number (6-digit) generated automatically.
// • The user has access to their utility company account using their username (or utility account
// number) and a password.
// • The user can log on to their utility account at any time and check their bill payment history,
// including the last 3 paid bills.
// • The user can also check the next bill payment amount and due date.

import java.util.Random;
import java.util.Scanner;

public class UtilityCompany {

    
    public static void CreateAccount(){
        Scanner scnr = new Scanner(System.in);
        Random rand = new Random();
        System.out.println("Create account:\n");

        System.out.print("Enter a Username: ");
        String username = scnr.next();

        System.out.print("Enter a Password: ");
        String password = scnr.next();

        System.out.printf("%s's, account number: " , username);
        System.out.println(rand.nextInt(1000000));
    }

    public void Login(){

    }

    public String paymentHistory(){
        return "";
    }

    public String nextPayment(){
        return "";
    }
    public static void main(String args[]){
        CreateAccount();
    }
}
