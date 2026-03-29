import models.Book;
import models.Library;
import models.User;
import services.BookService;
import services.LibraryService;
import services.UserService;

import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static BookService bookService;
    private static UserService userService;
    private static LibraryService libraryService;

    public static void main(String[] args) {
        Book bookModel = new Book();
        User userModel = new User();
        Library libraryModel = new Library();

        bookService = new BookService(bookModel);
        userService = new UserService(userModel);
        libraryService = new LibraryService(libraryModel, bookService, userService);

        boolean running = true;
        while (running) {
            System.out.println("\n1. Books");
            System.out.println("2. Users");
            System.out.println("3. Issue / Return");
            System.out.println("4. Reports");
            System.out.println("0. Exit");
            int choice = readInt("> ");
            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> userMenu();
                case 3 -> transactionMenu();
                case 4 -> reportsMenu();
                case 0 -> running = false;
                default -> System.out.println("Invalid.");
            }
        }
    }

    static void bookMenu() {
        System.out.println("\n1. All books");
        System.out.println("2. Search by ID");
        System.out.println("3. Search by Name");
        System.out.println("4. Search by Genre");
        System.out.println("5. Search by Author");
        System.out.println("6. Add book");
        System.out.println("7. Update book");
        System.out.println("8. Delete book");
        System.out.println("0. Back");
        int choice = readInt("> ");
        switch (choice) {
            case 1 -> {
                Map<Integer, Map<String, String>> books = bookService.getAllBooks();
                if (books.isEmpty()) { System.out.println("No books."); break; }
                books.forEach((id, b) ->
                    System.out.println(id + ". " + b.getOrDefault("name","-") + " by " + b.getOrDefault("author","-") + " (" + b.getOrDefault("copies","0") + " copies)"));
            }
            case 2 -> {
                int id = readInt("ID: ");
                printBook(bookService.getBookById(id), id);
            }
            case 3 -> printBookList(bookService.searchByName(readString("Name: ")));
            case 4 -> printBookList(bookService.searchByGenre(readString("Genre: ")));
            case 5 -> printBookList(bookService.searchByAuthor(readString("Author: ")));
            case 6 -> {
                String name   = readString("Name: ");
                String genre  = readString("Genre: ");
                String author = readString("Author: ");
                String year   = readString("Year: ");
                int copies    = readInt("Copies: ");
                System.out.println(bookService.addBook(name, genre, author, year, copies) ? "Added." : "Failed.");
            }
            case 7 -> {
                int id = readInt("ID: ");
                Map<String, String> b = bookService.getBookById(id);
                if (b == null) { System.out.println("Not found."); break; }
                String name   = readStringDefault("Name [" + b.getOrDefault("name","") + "]: ",   b.getOrDefault("name",""));
                String genre  = readStringDefault("Genre [" + b.getOrDefault("genre","") + "]: ", b.getOrDefault("genre",""));
                String author = readStringDefault("Author [" + b.getOrDefault("author","") + "]: ", b.getOrDefault("author",""));
                String year   = readStringDefault("Year [" + b.getOrDefault("year","") + "]: ",   b.getOrDefault("year",""));
                int copies    = readIntDefault("Copies [" + b.getOrDefault("copies","0") + "]: ", Integer.parseInt(b.getOrDefault("copies","0")));
                System.out.println(bookService.updateBook(id, name, genre, author, year, copies) ? "Updated." : "Failed.");
            }
            case 8 -> {
                int id = readInt("ID: ");
                System.out.println(bookService.removeBook(id) ? "Deleted." : "Failed.");
                
                
            }
            case 0 -> {}
            default -> System.out.println("Invalid.");
        }
    }

    static void userMenu() {
        System.out.println("\n1. All users");
        System.out.println("2. Search by ID");
        System.out.println("3. Search by Name");
        System.out.println("4. Register user");
        System.out.println("5. Update user");
        System.out.println("6. Borrowed books");
        System.out.println("7. Pay dues");
        System.out.println("0. Back");
        int choice = readInt("> ");
        switch (choice) {
            case 1 -> {
                Map<Integer, Map<String, String>> users = userService.getAllUsers();
                if (users.isEmpty()) { System.out.println("No users."); break; }
                users.forEach((id, u) ->
                    System.out.println(id + ". " + u.getOrDefault("name","-") + " | Class: " + u.getOrDefault("class","-") + " | Dues: Rs." + u.getOrDefault("due_fees","0")));
            }
            case 2 -> {
                int id = readInt("ID: ");
                printUser(userService.getUserById(id), id);
            }
            case 3 -> {
                List<Map<String, String>> results = userService.searchByName(readString("Name: "));
                if (results.isEmpty()) { System.out.println("Not found."); break; }
                results.forEach(u -> printUser(u, Integer.parseInt(u.get("id"))));
            }
            case 4 -> {
                String name = readString("Name: ");
                String cls  = readString("Class: ");
                System.out.println(userService.registerUser(name, cls) ? "Registered." : "Failed.");
            }
            case 5 -> {
                int id = readInt("ID: ");
                Map<String, String> u = userService.getUserById(id);
                if (u == null) { System.out.println("Not found."); break; }
                String name = readStringDefault("Name [" + u.getOrDefault("name","") + "]: ", u.getOrDefault("name",""));
                String cls  = readStringDefault("Class [" + u.getOrDefault("class","") + "]: ", u.getOrDefault("class",""));
                System.out.println(userService.updateUser(id, name, cls) ? "Updated." : "Failed.");
            }
            case 6 -> {
                int id = readInt("ID: ");
                if (userService.getUserById(id) == null) { System.out.println("Not found."); break; }
                List<Integer> bookIds = userService.getBorrowedBooks(id);
                if (bookIds.isEmpty()) { System.out.println("None borrowed."); break; }
                for (int bookId : bookIds) {
                    Map<String, String> b = bookService.getBookById(bookId);
                    System.out.println(bookId + ". " + (b != null ? b.getOrDefault("name","-") + " by " + b.getOrDefault("author","-") : "unknown"));
                }
            }
            case 7 -> {
                int id = readInt("ID: ");
                double dues = userService.getDueFees(id);
                System.out.println("Dues: Rs." + (int)dues);
                if (dues <= 0) break;
                System.out.println(libraryService.payFees(id, readDouble("Amount: Rs.")));
            }
            case 0 -> {}
            default -> System.out.println("Invalid.");
        }
    }

    static void transactionMenu() {
        System.out.println("\n1. Issue book");
        System.out.println("2. Return book");
        System.out.println("3. View transaction");
        System.out.println("4. Active loans");
        System.out.println("0. Back");
        int choice = readInt("> ");
        switch (choice) {
            case 1 -> System.out.println(libraryService.issueBook(readInt("User ID: "), readInt("Book ID: ")));
            case 2 -> System.out.println(libraryService.returnBook(readInt("Transaction ID: ")));
            case 3 -> {
                int txnId = readInt("Transaction ID: ");
                printTransaction(libraryService.getTransaction(txnId), txnId);
            }
            case 4 -> {
                List<Map<String, String>> active = libraryService.getActiveTransactions();
                if (active.isEmpty()) { System.out.println("No active loans."); break; }
                for (Map<String, String> txn : active)
                    printTransaction(txn, Integer.parseInt(txn.get("id")));
            }
            case 0 -> {}
            default -> System.out.println("Invalid.");
        }
    }

    static void reportsMenu() {
        System.out.println("\n1. All transactions");
        System.out.println("2. Transactions by user");
        System.out.println("3. Users with pending dues");
        System.out.println("4. Out-of-stock books");
        System.out.println("0. Back");
        int choice = readInt("> ");
        switch (choice) {
            case 1 -> {
                List<Map<String, String>> all = libraryService.getAllTransactions();
                if (all.isEmpty()) { System.out.println("No transactions."); break; }
                for (Map<String, String> txn : all)
                    printTransaction(txn, Integer.parseInt(txn.get("id")));
            }
            case 2 -> {
                List<Map<String, String>> txns = libraryService.getUserTransactions(readInt("User ID: "));
                if (txns.isEmpty()) { System.out.println("No transactions."); break; }
                for (Map<String, String> txn : txns)
                    printTransaction(txn, Integer.parseInt(txn.get("id")));
            }
            case 3 -> {
                boolean found = false;
                for (Map.Entry<Integer, Map<String, String>> e : userService.getAllUsers().entrySet()) {
                    double fees = Double.parseDouble(e.getValue().getOrDefault("due_fees","0"));
                    if (fees > 0) {
                        System.out.println(e.getKey() + ". " + e.getValue().getOrDefault("name","-") + " | Rs." + (int)fees);
                        found = true;
                    }
                }
                if (!found) System.out.println("No pending dues.");
            }
            case 4 -> {
                boolean found = false;
                for (Map.Entry<Integer, Map<String, String>> e : bookService.getAllBooks().entrySet()) {
                    if (Integer.parseInt(e.getValue().getOrDefault("copies","0")) == 0) {
                        System.out.println(e.getKey() + ". " + e.getValue().getOrDefault("name","-"));
                        found = true;
                    }
                }
                if (!found) System.out.println("All books in stock.");
            }
            case 0 -> {}
            default -> System.out.println("Invalid.");
        }
    }


    static void printBook(Map<String, String> b, int id) {
        if (b == null) { System.out.println("Not found."); return; }
        System.out.println("ID: " + id);
        System.out.println("Name: " + b.getOrDefault("name","-"));
        System.out.println("Genre: " + b.getOrDefault("genre","-"));
        System.out.println("Author: " + b.getOrDefault("author","-"));
        System.out.println("Year: " + b.getOrDefault("year","-"));
        System.out.println("Copies: " + b.getOrDefault("copies","0"));
    }

    static void printBookList(List<Map<String, String>> books) {
        if (books.isEmpty()) { System.out.println("Not found."); return; }
        for (Map<String, String> b : books)
            printBook(b, Integer.parseInt(b.getOrDefault("id","0")));
    }

    static void printUser(Map<String, String> u, int id) {
        if (u == null) { System.out.println("Not found."); return; }
        System.out.println("ID: " + id);
        System.out.println("Name: " + u.getOrDefault("name","-"));
        System.out.println("Class: " + u.getOrDefault("class","-"));
        System.out.println("Books: " + u.getOrDefault("books","none"));
        System.out.println("Dues: Rs." + u.getOrDefault("due_fees","0"));
    }

    static void printTransaction(Map<String, String> txn, int id) {
        if (txn == null) { System.out.println("Not found."); return; }
        System.out.println("Txn: " + id);
        System.out.println("User: " + txn.get("userId"));
        System.out.println("Book: " + txn.get("bookId"));
        System.out.println("Issued: " + txn.get("issueDate"));
        System.out.println("Due: " + txn.get("dueDate"));
        System.out.println("Returned: " + txn.get("returnDate"));
        System.out.println("Status: " + txn.get("status"));
    }



    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a number."); }
        }
    }

    static int readIntDefault(String prompt, int def) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return def;
        try { return Integer.parseInt(line); } catch (NumberFormatException e) { return def; }
    }

    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a number."); }
        }
    }

    static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    static String readStringDefault(String prompt, String def) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? def : line;
    }
}
