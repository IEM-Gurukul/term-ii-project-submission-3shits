package models;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class User {
    private Map<Integer, Map<String, String>> students = new HashMap<>();
    private static final String[] userMap = {"name", "class", "books", "due_fees"};
    private static final String FILE_PATH = "data/users.csv";

    public User() {
        loadFromCSV();
    }

    private void loadFromCSV() {
        students.clear();
        try (Scanner scan = new Scanner(new File(FILE_PATH))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;
                parseUserLine(line);
            }
        } catch (Exception e) {
            System.out.println("Cannot read users: " + e.getMessage());
        }
    }

    private void parseUserLine(String line) {
        HashMap<String, String> details = new HashMap<>();
        Integer id = 0;
        try (Scanner rowscan = new Scanner(line)) {
            rowscan.useDelimiter(",");
            int count = 0;
            while (rowscan.hasNext()) {
                String value = rowscan.next().trim();
                if (count == 0) {
                    id = Integer.parseInt(value);
                } else if (count - 1 < userMap.length) {
                    if (userMap[count - 1].equals("books")) {
                        
                        value = value.replace("[", "").replace("]", "").replace("/", ",");
                    }
                    details.put(userMap[count - 1], value);
                }
                count++;
            }
            students.put(id, details);
        } catch (Exception e) {
            System.out.println("Error reading user line: " + e.getMessage());
        }
    }

    public Map<Integer, Map<String, String>> getUsers() {
        return students;
    }

    public Map<String, String> searchUserById(int id) {
        Map<String, String> user = students.get(id);
        if (user != null) {
            Map<String, String> result = new HashMap<>(user);
            result.put("id", String.valueOf(id));
            return result;
        }
        return null;
    }

    public ArrayList<Map<String, String>> searchUsersByName(String name) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : students.entrySet()) {
            if (entry.getValue().getOrDefault("name", "").toLowerCase().contains(name.toLowerCase())) {
                Map<String, String> user = new HashMap<>(entry.getValue());
                user.put("id", String.valueOf(entry.getKey()));
                result.add(user);
            }
        }
        return result;
    }

    public int getNextId() {
        return students.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    public List<Integer> getBorrowedBookIds(int userId) {
        Map<String, String> user = students.get(userId);
        if (user == null) return new ArrayList<>();
        String booksStr = user.getOrDefault("books", "").trim();
        if (booksStr.isEmpty()) return new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (String s : booksStr.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) {
                try { ids.add(Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
            }
        }
        return ids;
    }

    public boolean addBookToUser(int userId, int bookId) {
        Map<String, String> user = students.get(userId);
        if (user == null) return false;
        List<Integer> borrowed = getBorrowedBookIds(userId);
        borrowed.add(bookId);
        user.put("books", listToBookString(borrowed));
        return updateUsersCSV(convertUserMap());
    }

    public boolean removeBookFromUser(int userId, int bookId) {
        Map<String, String> user = students.get(userId);
        if (user == null) return false;
        List<Integer> borrowed = getBorrowedBookIds(userId);
        borrowed.remove(Integer.valueOf(bookId));
        user.put("books", listToBookString(borrowed));
        return updateUsersCSV(convertUserMap());
    }

    public boolean addFee(int userId, double fee) {
        Map<String, String> user = students.get(userId);
        if (user == null) return false;
        double existing = Double.parseDouble(user.getOrDefault("due_fees", "0"));
        user.put("due_fees", String.valueOf((int)(existing + fee)));
        return updateUsersCSV(convertUserMap());
    }

    public boolean payFees(int userId, double amount) {
        Map<String, String> user = students.get(userId);
        if (user == null) return false;
        double existing = Double.parseDouble(user.getOrDefault("due_fees", "0"));
        double newFee = Math.max(0, existing - amount);
        user.put("due_fees", String.valueOf((int)newFee));
        return updateUsersCSV(convertUserMap());
    }

    private String listToBookString(List<Integer> ids) {
        List<String> strs = new ArrayList<>();
        for (Integer i : ids) strs.add(String.valueOf(i));
        return String.join(",", strs);
    }

    public boolean updateStudent(List<String> student, int id) {
        if (students.getOrDefault(id, null) == null) return false;
        Map<String, String> user = new HashMap<>();
        for (int i = 0; i < student.size() && i < userMap.length; i++) {
            user.put(userMap[i], student.get(i));
        }
        students.put(id, user);
        return updateUsersCSV(convertUserMap());
    }

    public boolean updateUsersCSV(List<List<String>> studentsList) {
        try (FileWriter overwrite = new FileWriter(FILE_PATH)) {
            for (List<String> std : studentsList) {
                overwrite.append(String.join(",", std));
                overwrite.append("\n");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error Updating: " + e.getMessage());
            return false;
        }
    }

    public List<List<String>> convertUserMap() {
        List<List<String>> newstudents = new ArrayList<>();
        for (int studentId : students.keySet()) {
            List<String> newstd = new ArrayList<>();
            newstd.add(String.valueOf(studentId));
            Map<String, String> details = students.get(studentId);
            for (String key : userMap) {
                String val = details.getOrDefault(key, "");
                if (key.equals("books")) {
                   
                    if (!val.isEmpty()) {
                        val = "[" + val.replace(",", "/") + "]";
                    } else {
                        val = "[]";
                    }
                }
                newstd.add(val);
            }
            newstudents.add(newstd);
        }
        return newstudents;
    }

    public boolean insertUser(List<String> user) {
        try (FileWriter csv = new FileWriter(FILE_PATH, true)) {
            csv.append(String.join(",", user));
            csv.append("\n");
            csv.flush();
            parseUserLine(String.join(",", user));
            return true;
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    public static String[] getUserMap() {
        return userMap;
    }
}
