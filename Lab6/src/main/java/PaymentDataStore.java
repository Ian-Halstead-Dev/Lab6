import java.io.*;
import java.util.*;

public class PaymentDataStore {
    private static final String FILE_PATH = "paymentHistory.txt";

    // Save all users' payment histories using this class.
    //Payment History is stored as AccNum:Amount,Amount,Amount
    public static void savePaymentHistories(Set<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                ArrayDeque<Integer> history = user.getPaymentHistoy();
                writer.write(user.getAccNum() + ":");

                Iterator<Integer> it = history.iterator();
                while (it.hasNext()) {
                    writer.write(it.next().toString());
                    if (it.hasNext()) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load payment histories and apply to matching users
    public static void loadPaymentHistories(Set<User> users) {
        Map<Integer, ArrayDeque<Integer>> historyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(":");
                if (split.length != 2) continue;

                int accNum = Integer.parseInt(split[0]);
                String[] amounts = split[1].split(",");

                ArrayDeque<Integer> deque = new ArrayDeque<>();
                for (String amount : amounts) {
                    deque.add(Integer.parseInt(amount));
                }
                historyMap.put(accNum, deque);
            }
        } catch (FileNotFoundException e) {
            // No history yet; safe to ignore
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply to matching users
        for (User user : users) {
            if (historyMap.containsKey(user.getAccNum())) {
                user.setPaymentHistory(historyMap.get(user.getAccNum()));
            }
        }
    }
}