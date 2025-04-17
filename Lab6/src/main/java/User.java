import java.util.ArrayDeque;
import java.util.Random;
//This class implements the user with their information and payment history and accounts.
public class User {
    private String username;
    private String password;
    private int accNum;

    private ArrayDeque<Integer> paymentHistory = new ArrayDeque<>();
    private int nextPayment = -1;
    private Checking checkingAcct = new Checking();
    private Saving savingAcct = new Saving();
    public User(){
        username = "";
        password = "";
        accNum = 0;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getAccNum() { return accNum; }
    public void setAccNum(int accNum) { this.accNum = accNum; }

    public ArrayDeque<Integer> getPaymentHistoy() { return paymentHistory; }
    public void setPaymentHistory(ArrayDeque<Integer> paymentHistoy) { this.paymentHistory = paymentHistoy; }
    public int getNextPayment() {
        if(nextPayment == -1) {
        Random rand = new Random();
        nextPayment = 1 + rand.nextInt(99);
        }
        return nextPayment;
    }
    public void setNextPayment(int nextPayment) { this.nextPayment = nextPayment; }
    public Checking getCheckingAcct() { return checkingAcct; }
    public void setCheckingAcct(Checking checkingAcct) { this.checkingAcct = checkingAcct; }
    public Saving getSavingAcct() { return savingAcct; }
    public void setSavingAcct(Saving savingAcct) { this.savingAcct = savingAcct; }
}
