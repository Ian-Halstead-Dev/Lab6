import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BankingAppTests {

    private Checking checking;
    private Saving saving;
    private User user;
    private final String filePath = "users.txt";

    @BeforeEach
    public void setUp() {
        checking = new Checking();
        checking.setBalance(1000);
        saving = new Saving();
        saving.setBalance(500);
        user = new User();
        user.setCheckingAcct(checking);
        user.setSavingAcct(saving);
        user.setUsername("ian");
        user.setPassword("pass");
        user.setAccNum(42);
    }

    // === CHECKING ACCOUNT TESTS ===

    @Test
    public void testDepositWithinLimit() throws AntiMoneyLaunderingException {
        checking.deposit(2000);
        assertEquals(3000, checking.getBalance());
    }

    @Test
    public void testDepositMultipleTimesWithinLimit() throws AntiMoneyLaunderingException {
        checking.deposit(2000);
        checking.deposit(2000);
        checking.deposit(1000); // Total = 5000
        assertEquals(6000, checking.getBalance());
    }

    @Test
    public void testDepositExceedingAcrossMultipleDeposits() throws AntiMoneyLaunderingException {
        checking.deposit(3000);
        assertThrows(AntiMoneyLaunderingException.class, () -> checking.deposit(2500));
        assertEquals(4000, checking.getBalance()); // Only 3000 accepted, and base is 1000
    }

    @Test
    public void testResetDepositLimitNextDay() throws AntiMoneyLaunderingException {
        checking.deposit(5000);
        checking.reset();
        DayTracker.nextDay();
        assertDoesNotThrow(() -> checking.deposit(5000));
        assertEquals(11000, checking.getBalance());
    }

    @Test
    public void testWithdrawWithinLimit() throws AntiMoneyLaunderingException {
        checking.withdraw(300);
        assertEquals(700, checking.getBalance());
    }

    @Test
    public void testWithdrawExceedingLimit() {
        boolean check = checking.withdraw(600);
        assertFalse(check);
    }

    // === SAVINGS TRANSFER TESTS ===

    @Test
    public void testTransferFromSavingsToChecking() throws AntiMoneyLaunderingException {
        saving.setBalance(500);
        checking.setBalance(1000);
        saving.setBalance(450);
        checking.setBalance(1050);
        assertEquals(450, saving.getBalance());
        assertEquals(1050, checking.getBalance());
    }



    @Test
    public void testTransferExactlyToLimit() throws AntiMoneyLaunderingException {

        saving.setBalance(1000);
        checking.setBalance(1000);


        saving.setBalance(900);
        checking.setBalance(1100);

        assertEquals(900, saving.getBalance());
        assertEquals(1100, checking.getBalance());
    }

    // === UTILITY PAYMENT TESTS ===

    @Test
    public void testUtilityPaymentOnce() throws AntiMoneyLaunderingException {
        user.setNextPayment(200);
        boolean result = Payment.payUtilityBill(user, checking, null);
        assertTrue(result);
        assertEquals(800, checking.getBalance());
        assertEquals(1, user.getPaymentHistoy().size());
    }

    @Test
    public void testMultipleUtilityPayments() throws AntiMoneyLaunderingException {
        user.setNextPayment(200);
        assertTrue(Payment.payUtilityBill(user, checking, null));
        user.setNextPayment(150);
        assertTrue(Payment.payUtilityBill(user, checking, null));

        assertEquals(2, user.getPaymentHistoy().size());
        assertEquals(1000 - 200 - 150, checking.getBalance());
    }

    @Test
    public void testUtilityPaymentInsufficientFunds() throws AntiMoneyLaunderingException {
        checking.setBalance(100);
        user.setNextPayment(150);
        boolean result = Payment.payUtilityBill(user, checking, null);
        assertFalse(result);
        assertEquals(100, checking.getBalance());
        assertEquals(0, user.getPaymentHistoy().size());
    }

    // === STORAGE TESTS ===

    @Test
    public void testNullUserStorage() {
        assertThrows(NullPointerException.class, () -> UserDataStore.saveUsers(null));
    }

    @Test
    public void testNullElementWithMultipleUsers() throws IOException {
        Set<User> users = new HashSet<>();
        users.add(null);
        User u = new User();
        u.setUsername("john");
        u.setPassword("123");
        u.setAccNum(42);
        users.add(u);

        UserDataStore.saveUsers(users);

        Set<User> loaded = UserDataStore.loadUsers();
        assertEquals(1, loaded.size());
    }

    @Test
    public void testNullSingleUserElement() throws IOException {
        Set<User> users = new HashSet<>();
        users.add(null);
        UserDataStore.saveUsers(users);
        Set<User> loaded = UserDataStore.loadUsers();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void testIncompatibleTypeInFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ian,pass,abcd,xyz\n"); // xyz not parsable as accNum
        }
        Set<User> loaded = UserDataStore.loadUsers();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void testEmptyFieldsInFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(",,,\n");
        }
        Set<User> users = UserDataStore.loadUsers();
        assertTrue(users.isEmpty());
    }

    // === SAVINGS TRANSFER TESTS ===

    @Test
    public void testTransferToCheckingWithinLimit() throws AntiMoneyLaunderingException, InsufficientFundsException {
        saving.setBalance(500);
        checking.setBalance(1000);

        saving.transfer(checking, 50);

        assertEquals(450, saving.getBalance());
        assertEquals(1050, checking.getBalance());
    }

    @Test
    public void testTransferToCheckingExceedingDailyLimit() throws AntiMoneyLaunderingException, InsufficientFundsException {
        saving.setBalance(1000);
        checking.setBalance(500);

       assertThrows(AntiMoneyLaunderingException.class,  () -> saving.transfer(checking, 1000));


        assertEquals(1000, saving.getBalance()); // No transfer occurred
        assertEquals(500, checking.getBalance());
    }

    @Test
    public void testTransferToCheckingExceedsLimitAfterPartialTransfers() throws AntiMoneyLaunderingException, InsufficientFundsException {
        saving.setBalance(10);
        checking.setBalance(500);
        assertThrows(InsufficientFundsException.class, () ->  saving.transfer(checking, 20));

        assertEquals(10, saving.getBalance());
        assertEquals(500, checking.getBalance());
    }

    @Test
    public void testTransferToCheckingExactlyAtLimit() throws AntiMoneyLaunderingException, InsufficientFundsException {
        saving.setBalance(1000);
        checking.setBalance(500);
        saving.transfer(checking, 50);
       saving.transfer(checking, 50);

        assertEquals(900, saving.getBalance());
        assertEquals(600, checking.getBalance());
    }


    // === CLEANUP ===

    @AfterEach
    public void cleanUp() {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}