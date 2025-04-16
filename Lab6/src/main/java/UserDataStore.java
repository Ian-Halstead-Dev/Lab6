import java.io.*;
import java.util.*;

public class UserDataStore {
    private static final String FILE_PATH = "users.txt";

    public static void saveUsers(Set<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                int checkingBalance = user.getCheckingAcct() != null ? user.getCheckingAcct().getBalance() : 0;
                int savingBalance = user.getSavingAcct() != null ? user.getSavingAcct().getBalance() : 0;
                writer.write(user.getPin() + "," +
                        user.getUsername() + "," +
                        user.getPassword() + "," +
                        user.getAccNum() + "," +
                        checkingBalance + "," +
                        savingBalance);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<User> loadUsers() {
        Set<User> users = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    User user = new User();
                    user.setPin(Integer.parseInt(parts[0]));
                    user.setUsername(parts[1]);
                    user.setPassword(parts[2]);
                    user.setAccNum(Integer.parseInt(parts[3]));
                    Checking checking = new Checking();
                    checking.setBalance(Integer.parseInt(parts[4]));
                    Saving saving = new Saving();
                    saving.setBalance(Integer.parseInt(parts[5]));
                    user.setCheckingAcct(checking);
                    user.setSavingAcct(saving);
                    users.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            // Return empty set if file doesn't exist
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}
