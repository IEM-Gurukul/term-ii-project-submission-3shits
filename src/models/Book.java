package models;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Book {
    private Map<Integer, Map<String, String>> books = new HashMap<>();
    private Map<String, ArrayList<Integer>> names = new HashMap<>();
    private static final String[] detailMap = {"name", "genre", "author", "year", "copies"};
    private static final String FILE_PATH = "data/books.csv";

    public Book() {
        loadFromCSV();
    }

    private void loadFromCSV() {
        books.clear();
        names.clear();
        try (Scanner scan = new Scanner(new File(FILE_PATH))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;
                parseBookLine(line);
            }
        } catch (Exception e) {
            System.out.println("Cannot read books: " + e.getMessage());
        }
    }

    private void parseBookLine(String line) {
        HashMap<String, String> details = new HashMap<>();
        Integer id = 0;
        try (Scanner rowscan = new Scanner(line)) {
            rowscan.useDelimiter(",");
            int count = 0;
            while (rowscan.hasNext()) {
                String value = rowscan.next().trim();
                if (count == 0) {
                    id = Integer.parseInt(value);
                } else if (count - 1 < detailMap.length) {
                    details.put(detailMap[count - 1], value);
                }
                count++;
            }
            books.put(id, details);
            names.computeIfAbsent(details.getOrDefault("name", ""), k -> new ArrayList<>()).add(id);
        } catch (Exception e) {
            System.out.println("Error reading book line: " + e.getMessage());
        }
    }

    public Map<Integer, Map<String, String>> getBooks() {
        return books;
    }

    public Map<String, String> searchBookByID(int id) {
        Map<String, String> book = books.getOrDefault(id, null);
        if (book != null) {
            Map<String, String> result = new HashMap<>(book);
            result.put("id", String.valueOf(id));
            return result;
        }
        return null;
    }

    public ArrayList<Map<String, String>> searchBooksByName(String name) {
        ArrayList<Map<String, String>> searched = new ArrayList<>();
        
        for (Map.Entry<String, ArrayList<Integer>> entry : names.entrySet()) {
            if (entry.getKey().toLowerCase().contains(name.toLowerCase())) {
                for (Integer i : entry.getValue()) {
                    Map<String, String> book = new HashMap<>(books.get(i));
                    book.put("id", String.valueOf(i));
                    searched.add(book);
                }
            }
        }
        return searched;
    }

    public ArrayList<Map<String, String>> searchBooksByGenre(String genre) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : books.entrySet()) {
            if (entry.getValue().getOrDefault("genre", "").toLowerCase().contains(genre.toLowerCase())) {
                Map<String, String> book = new HashMap<>(entry.getValue());
                book.put("id", String.valueOf(entry.getKey()));
                result.add(book);
            }
        }
        return result;
    }

    public ArrayList<Map<String, String>> searchBooksByAuthor(String author) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> entry : books.entrySet()) {
            if (entry.getValue().getOrDefault("author", "").toLowerCase().contains(author.toLowerCase())) {
                Map<String, String> book = new HashMap<>(entry.getValue());
                book.put("id", String.valueOf(entry.getKey()));
                result.add(book);
            }
        }
        return result;
    }

    public int getNextId() {
        return books.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    public boolean insertBook(List<String> book) {
        try (FileWriter csv = new FileWriter(FILE_PATH, true)) {
            csv.append(String.join(",", book));
            csv.append("\n");
            csv.flush();
            parseBookLine(String.join(",", book));
            return true;
        } catch (Exception e) {
            System.out.println("Error inserting book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBookByID(int id) {
        if (!books.containsKey(id)) return false;
        String bookName = books.get(id).getOrDefault("name", "");
        books.remove(id);
        if (names.containsKey(bookName)) {
            names.get(bookName).remove(Integer.valueOf(id));
            if (names.get(bookName).isEmpty()) names.remove(bookName);
        }
        return updateBooksCSV(convertBookMap());
    }

    public boolean updateBooksCSV(List<List<String>> booksList) {
        try (FileWriter overwrite = new FileWriter(FILE_PATH)) {
            for (List<String> book : booksList) {
                overwrite.append(String.join(",", book));
                overwrite.append("\n");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error Updating: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(int id, List<String> bookDetails) {
        if (books.getOrDefault(id, null) == null) return false;
        Map<String, String> bookMap = new HashMap<>();
        for (int i = 0; i < bookDetails.size() && i < detailMap.length; i++) {
            bookMap.put(detailMap[i], bookDetails.get(i));
        }
        books.put(id, bookMap);
        return updateBooksCSV(convertBookMap());
    }

    public boolean decrementCopies(int id) {
        Map<String, String> book = books.get(id);
        if (book == null) return false;
        int copies = Integer.parseInt(book.getOrDefault("copies", "0"));
        if (copies <= 0) return false;
        book.put("copies", String.valueOf(copies - 1));
        return updateBooksCSV(convertBookMap());
    }

    public boolean incrementCopies(int id) {
        Map<String, String> book = books.get(id);
        if (book == null) return false;
        int copies = Integer.parseInt(book.getOrDefault("copies", "0"));
        book.put("copies", String.valueOf(copies + 1));
        return updateBooksCSV(convertBookMap());
    }

    public List<List<String>> convertBookMap() {
        List<List<String>> newbooks = new ArrayList<>();
        for (int bookId : books.keySet()) {
            List<String> newbook = new ArrayList<>();
            newbook.add(String.valueOf(bookId));
            Map<String, String> details = books.get(bookId);
            for (String key : detailMap) {
                newbook.add(details.getOrDefault(key, ""));
            }
            newbooks.add(newbook);
        }
        return newbooks;
    }

    public static String[] getDetailMap() {
        return detailMap;
    }
}
