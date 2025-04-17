import java.io.*;
import java.util.*;

public class PinDataStore {
    private static final String FILE_PATH = "pins.txt";

    // Save PINs for each user’s checking account
    public static void savePins(Set<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                Checking checking = user.getCheckingAcct();
                if (checking != null) {
                    writer.write(user.getAccNum() + ":" + checking.getPin());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load PINs and assign them to users' checking accounts
    public static void loadPins(Set<User> users) {
        Map<Integer, Integer> pinMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int accNum = Integer.parseInt(parts[0]);
                    int pin = Integer.parseInt(parts[1]);
                    pinMap.put(accNum, pin);
                }
            }
        } catch (FileNotFoundException e) {
            // No file yet? No problem.
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (User user : users) {
            if (pinMap.containsKey(user.getAccNum())) {
                user.getCheckingAcct().setPin(pinMap.get(user.getAccNum()));
            }
        }
    }
}