package models;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library {
    private Map<Integer, Map<String, String>> transactions = new HashMap<>();
    private static final String FILE_PATH = "data/library.csv";
    private static final String[] txnMap = {"userId", "bookId", "issueDate", "dueDate", "returnDate", "status"};

    public Library() {
        loadFromCSV();
    }

    private void loadFromCSV() {
        transactions.clear();
        try (Scanner scan = new Scanner(new File(FILE_PATH))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;
                parseTxnLine(line);
            }
        } catch (Exception e) {
            
        }
    }

    private void parseTxnLine(String line) {
        try (Scanner rowscan = new Scanner(line)) {
            rowscan.useDelimiter(",");
            Map<String, String> details = new HashMap<>();
            int count = 0;
            int id = 0;
            while (rowscan.hasNext()) {
                String value = rowscan.next().trim();
                if (count == 0) {
                    id = Integer.parseInt(value);
                } else if (count - 1 < txnMap.length) {
                    details.put(txnMap[count - 1], value);
                }
                count++;
            }
            transactions.put(id, details);
        } catch (Exception e) {
            System.out.println("Error reading transaction: " + e.getMessage());
        }
    }

    public int issueBook(int userId, int bookId) {
        int txnId = getNextId();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String dueDate = LocalDate.now().plusDays(14).format(DateTimeFormatter.ISO_DATE);
        Map<String, String> txn = new HashMap<>();
        txn.put("userId", String.valueOf(userId));
        txn.put("bookId", String.valueOf(bookId));
        txn.put("issueDate", today);
        txn.put("dueDate", dueDate);
        txn.put("returnDate", "-");
        txn.put("status", "ISSUED");
        transactions.put(txnId, txn);
        saveTxnToCSV(txnId, txn);
        return txnId;
    }

    public boolean returnBook(int txnId) {
        Map<String, String> txn = transactions.get(txnId);
        if (txn == null || txn.get("status").equals("RETURNED")) return false;
        txn.put("returnDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        txn.put("status", "RETURNED");
        updateLibraryCSV();
        return true;
    }

    public Map<String, String> getTransaction(int txnId) {
        Map<String, String> txn = transactions.get(txnId);
        if (txn != null) {
            Map<String, String> result = new HashMap<>(txn);
            result.put("id", String.valueOf(txnId));
            return result;
        }
        return null;
    }

    public List<Map<String, String>> getTransactionsByUser(int userId) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : transactions.entrySet()) {
            if (entry.getValue().get("userId").equals(String.valueOf(userId))) {
                Map<String, String> txn = new HashMap<>(entry.getValue());
                txn.put("id", String.valueOf(entry.getKey()));
                result.add(txn);
            }
        }
        return result;
    }

    public List<Map<String, String>> getActiveTransactions() {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : transactions.entrySet()) {
            if ("ISSUED".equals(entry.getValue().get("status"))) {
                Map<String, String> txn = new HashMap<>(entry.getValue());
                txn.put("id", String.valueOf(entry.getKey()));
                result.add(txn);
            }
        }
        return result;
    }

    public List<Map<String, String>> getAllTransactions() {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : transactions.entrySet()) {
            Map<String, String> txn = new HashMap<>(entry.getValue());
            txn.put("id", String.valueOf(entry.getKey()));
            result.add(txn);
        }
        return result;
    }

    public int getActiveTransactionForUserBook(int userId, int bookId) {
        for (Map.Entry<Integer, Map<String, String>> entry : transactions.entrySet()) {
            Map<String, String> txn = entry.getValue();
            if (txn.get("userId").equals(String.valueOf(userId))
                && txn.get("bookId").equals(String.valueOf(bookId))
                && "ISSUED".equals(txn.get("status"))) {
                return entry.getKey();
            }
        }
        return -1;
    }

    
    public long getDaysOverdue(int txnId) {
        Map<String, String> txn = transactions.get(txnId);
        if (txn == null) return 0;
        try {
            LocalDate due = LocalDate.parse(txn.get("dueDate"), DateTimeFormatter.ISO_DATE);
            return LocalDate.now().toEpochDay() - due.toEpochDay();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getNextId() {
        return transactions.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    private void saveTxnToCSV(int id, Map<String, String> txn) {
        try (FileWriter csv = new FileWriter(FILE_PATH, true)) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(id));
            for (String key : txnMap) row.add(txn.getOrDefault(key, ""));
            csv.append(String.join(",", row));
            csv.append("\n");
        } catch (Exception e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    private void updateLibraryCSV() {
        try (FileWriter overwrite = new FileWriter(FILE_PATH)) {
            for (Map.Entry<Integer, Map<String, String>> entry : transactions.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(entry.getKey()));
                for (String key : txnMap) row.add(entry.getValue().getOrDefault(key, ""));
                overwrite.append(String.join(",", row));
                overwrite.append("\n");
            }
        } catch (Exception e) {
            System.out.println("Error updating library CSV: " + e.getMessage());
        }
    }
}
