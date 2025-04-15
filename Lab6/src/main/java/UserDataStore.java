import java.io.*;
import java.util.*;

public class UserDataStore {
    private static final String FILE_PATH = "users.txt";

    public static void saveUsers(Set<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                int balance = user.getCheckingAcct() != null ? user.getCheckingAcct().getBalance() : 0;
                writer.write(user.getUsername() + "," +
                        user.getPassword() + "," +
                        user.getAccNum() + "," +
                        balance);
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
                if (parts.length == 4) {
                    User user = new User();
                    user.setUsername(parts[0]);
                    user.setPassword(parts[1]);
                    user.setAccNum(Integer.parseInt(parts[2]));
                    Checking checking = new Checking();
                    checking.setBalance(Integer.parseInt(parts[3]));
                    user.setCheckingAcct(checking);
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
