// The Utility Company
// • The user will create a new account at the utility company using a username and a password.
// • The utility company will give the user a new account number (6-digit) generated automatically.
// • The user has access to their utility company account using their username (or utility account
// number) and a password.
// • The user can log on to their utility account at any time and check their bill payment history,
// including the last 3 paid bills.
// • The user can also check the next bill payment amount and due date.

import java.util.*;

public class UtilityCompany {

    //Bad database practice
    private static Set<User> users = new HashSet<>();

    
    public static void CreateAccount(){
        Scanner scnr = new Scanner(System.in);
        Random rand = new Random();
        User user = new User();
        users.add(user);
        System.out.println("Create account:\n");

        System.out.print("Enter a Username: ");
        String uname = scnr.next();
        for(User u : users){
            while(Objects.equals(uname, u.getUsername())){
                System.out.println("Username taken, enter another:");
                uname = scnr.next();
            }
        }
        user.setUsername(uname);

        System.out.print("Enter a Password: ");
        user.setPassword(scnr.next());

        user.setAccNum(rand.nextInt(1000000));
        System.out.printf("%s's, account number: " , user.getUsername());
        System.out.println(user.getAccNum());
    }

    public static void Login(){
        Scanner scnr = new Scanner(System.in);
        System.out.println("Login:\n");

        System.out.print("Enter your Username or Account Number: ");
        String input = scnr.next();

        System.out.print("Enter a Password: ");
        String password = scnr.next();

        for(User u : users){
            if(Objects.equals(input, u.getUsername()) || Objects.equals(input, Integer.toString(u.getAccNum()))){
                if(Objects.equals(password, u.getPassword())){
                    System.out.println("Login Successful! Welcome " + u.getUsername());
                }


            }
        }
    }

    public String paymentHistory(){
        return "";
    }

    public String nextPayment(){
        return "";
    }
    public static void main(String args[]){
        CreateAccount();
        CreateAccount();
        Login();
    }
}
