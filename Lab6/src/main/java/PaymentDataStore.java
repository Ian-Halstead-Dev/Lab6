import java.io.*;
import java.util.*;

public class PaymentDataStore {
    private static final String FILE_PATH = "paymentHistory.txt";

    /**
     * Save all users' payment histories and next payments to the file.
     * Format: accNum:nextPayment:amount,amount,amount
     */
    public static void savePaymentHistories(Set<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                int accNum = user.getAccNum();
                int nextPayment = user.getNextPayment();
                ArrayDeque<Integer> history = user.getPaymentHistoy();

                StringBuilder line = new StringBuilder();
                line.append(accNum).append(":").append(nextPayment).append(":");

                Iterator<Integer> it = history.iterator();
                while (it.hasNext()) {
                    line.append(it.next());
                    if (it.hasNext()) {
                        line.append(",");
                    }
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving payment histories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load payment histories and next payments from the file into the provided users.
     */
    public static void loadPaymentHistories(Set<User> users) {
        Map<Integer, ArrayDeque<Integer>> historyMap = new HashMap<>();
        Map<Integer, Integer> nextPaymentMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");

                if (parts.length < 3) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    int accNum = Integer.parseInt(parts[0]);
                    int nextPayment = Integer.parseInt(parts[1]);
                    ArrayDeque<Integer> history = new ArrayDeque<>();
                    if (!parts[2].isEmpty()) {
                        String[] amounts = parts[2].split(",");
                        for (String amount : amounts) {
                            history.add(Integer.parseInt(amount));
                        }
                    }

                    nextPaymentMap.put(accNum, nextPayment);
                    historyMap.put(accNum, history);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping line with invalid number: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // No file exists yet — that's fine.
        } catch (IOException e) {
            System.err.println("Error reading payment histories: " + e.getMessage());
            e.printStackTrace();
        }

        for (User user : users) {
            int accNum = user.getAccNum();

            ArrayDeque<Integer> history = historyMap.get(accNum);
            Integer next = nextPaymentMap.get(accNum);

            if (history != null) {
                user.setPaymentHistory(history);
            }

            if (next != null) {
                user.setNextPayment(next);
            }
        }
    }
}