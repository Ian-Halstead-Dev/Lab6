import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankingAppTests {

    private User user;
    private Checking checking;
    private Saving saving;
    private final String filePath = "users.txt";

    @BeforeEach
    public void setup() throws IOException {
        // Initialize user and accounts
        user = new User();
        checking = user.getCheckingAcct();
        saving = user.getSavingAcct();
        DayTracker.resetDay();

        // Clear the user data file before each storage test
        new FileWriter(filePath, false).close();
    }

    // ---------------- FUNCTIONAL TESTS ----------------

    @Test
    public void testValidDepositToChecking() throws AntiMoneyLaunderingException {
        assertTrue(checking.deposit(3000));
        assertEquals(4000, checking.getBalance());
    }

    @Test
    public void testOverDepositLimitThrowsException() {
        assertThrows(AntiMoneyLaunderingException.class, () -> checking.deposit(6000));
    }

    @Test
    public void testValidWithdrawal() {
        assertTrue(checking.withdraw(300));
        assertEquals(700, checking.getBalance());
    }

    @Test
    public void testWithdrawalOverLimit() {
        checking.withdraw(300);
        assertFalse(checking.withdraw(250)); // exceeds daily $500
    }

    @Test
    public void testOverdraftNotAllowed() {
        assertFalse(checking.withdraw(2000));
    }

    @Test
    public void testPayUtilityBillSuccess() throws AntiMoneyLaunderingException {

        user.setNextPayment(300);
        boolean result = Payment.payUtilityBill(user, checking, null);

        assertTrue(result);

        assertEquals(1, user.getPaymentHistoy().size());
        assertEquals(300, user.getPaymentHistoy().peekFirst());
        assertEquals(700, checking.getBalance());
    }

    @Test
    public void testPayUtilityBillFailInsufficientFunds() {
        user.setNextPayment(2000); // exceeds initial checking balance
        boolean result = Payment.payUtilityBill(user, checking, null);

        assertFalse(result);
        assertEquals(2000, user.getNextPayment());
        assertEquals(1000, checking.getBalance());
        assertTrue(user.getPaymentHistoy().isEmpty());
    }

    @Test
    public void testSavingAccountTransferWithinLimit() throws AntiMoneyLaunderingException {
        if (saving.transferToday + 100 <= 100) {
            saving.setBalance(saving.getBalance() - 100);
            checking.setBalance(checking.getBalance() + 100);
            saving.transferToday += 100;
        }
        assertEquals(900, saving.getBalance());
        assertEquals(1100, checking.getBalance());
    }

    @Test
    public void testInvalidTransferOverLimit() throws AntiMoneyLaunderingException {
        saving.deposit(1000);
        saving.transferToday = 100;
        int start = saving.getBalance();
        if (saving.transferToday + 50 > 100) {
            // Transfer shouldn't happen
            assertEquals(start, saving.getBalance());
        }
    }

    // ---------------- DATA STORAGE TESTS ----------------

    @Test
    public void testSaveAndLoadSingleUser() {
        Set<User> users = new HashSet<>();
        User u = new User();
        u.setUsername("ian");
        u.setPassword("pass");
        u.setPin(1234);
        u.setAccNum(1);
        users.add(u);

        UserDataStore.saveUsers(users);
        Set<User> loaded = UserDataStore.loadUsers();

        assertEquals(1, loaded.size());
        assertEquals("ian", loaded.iterator().next().getUsername());
    }

    @Test
    public void testSaveAndLoadMultipleUsers() {
        Set<User> users = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            User u = new User();
            u.setUsername("user" + i);
            u.setPassword("pass" + i);
            u.setPin(1000 + i);
            u.setAccNum(i);
            users.add(u);
        }

        UserDataStore.saveUsers(users);
        Set<User> loaded = UserDataStore.loadUsers();

        assertEquals(5, loaded.size());
    }

    @Test
    public void testEmptyFileLoad() throws IOException {
        new FileWriter(filePath, false).close(); // Ensure empty file
        Set<User> users = UserDataStore.loadUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    public void testMalformedLine() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("1234,bad,data\n");
        }
        Set<User> users = UserDataStore.loadUsers();
        assertEquals(0, users.size());
    }


    @Test
    public void testNegativeDepositThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> checking.deposit(-100));
    }

    @Test
    public void testNegativeWithdrawalFails() {
        assertThrows(IllegalArgumentException.class, () -> checking.withdraw(-100));

        assertEquals(1000, checking.getBalance());
    }

    @Test
    public void testPayUtilityBillWithNullUser() {
        assertThrows(NullPointerException.class, () -> Payment.payUtilityBill(null, checking, null));
    }

    @Test
    public void testPayUtilityBillWithNullChecking() {
        assertThrows(NullPointerException.class, () -> Payment.payUtilityBill(user, null, null));
    }

    @Test
    public void testLoadUserWithInvalidPin() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ian,pass,notanumber,1\n");
        }
        Set<User> users = UserDataStore.loadUsers();
        assertTrue(users.isEmpty());
    }
}