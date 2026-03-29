package services;

import models.Library;
import java.util.*;

public class LibraryService {
    private final Library libraryModel;
    private final BookService bookService;
    private final UserService userService;
    private static final double FINE_PER_DAY = 5.0;

    public LibraryService(Library libraryModel, BookService bookService, UserService userService) {
        this.libraryModel = libraryModel;
        this.bookService = bookService;
        this.userService = userService;
    }

    public String issueBook(int userId, int bookId) {
        
        if (userService.getUserById(userId) == null) {
            return "User not found.";
        }
        
        if (bookService.getBookById(bookId) == null) {
            return "Book not found.";
        }
        
        if (!bookService.isAvailable(bookId)) {
            return "No copies available for this book.";
        }
        
        if (!userService.canBorrow(userId)) {
            return "User has reached the maximum borrow limit";
        }
        
        List<Integer> borrowed = userService.getBorrowedBooks(userId);
        if (borrowed.contains(bookId)) {
            return " User already has this book issued.";
        }

        int txnId = libraryModel.issueBook(userId, bookId);
        bookService.decrementCopies(bookId);
        userService.addBookToUser(userId, bookId);

        return " Book issued. Transaction ID: " + txnId + ". Due in 14 days.";
    }

    public String returnBook(int txnId) {
        Map<String, String> txn = libraryModel.getTransaction(txnId);
        if (txn == null) {
            return " Transaction not found.";
        }
        if ("RETURNED".equals(txn.get("status"))) {
            return " Book already returned.";
        }

        int userId = Integer.parseInt(txn.get("userId"));
        int bookId = Integer.parseInt(txn.get("bookId"));

        long overdueDays = libraryModel.getDaysOverdue(txnId);
        double fine = 0;
        if (overdueDays > 0) {
            fine = overdueDays * FINE_PER_DAY;
            userService.addFee(userId, fine);
        }

        libraryModel.returnBook(txnId);
        bookService.incrementCopies(bookId);
        userService.removeBookFromUser(userId, bookId);

        if (fine > 0) {
            return " Book returned. Overdue by " + overdueDays + " day(s). Fine added: ₹" + (int)fine;
        }
        return " Book returned on time. No fine.";
    }

    public String payFees(int userId, double amount) {
        double current = userService.getDueFees(userId);
        if (current <= 0) return "INFO: No pending fees.";
        if (amount > current) amount = current;
        userService.payFees(userId, amount);
        return " Payment of ₹" + (int)amount + " received. Remaining fees: ₹" + (int)(current - amount);
    }

    public List<Map<String, String>> getActiveTransactions() {
        return libraryModel.getActiveTransactions();
    }

    public List<Map<String, String>> getAllTransactions() {
        return libraryModel.getAllTransactions();
    }

    public List<Map<String, String>> getUserTransactions(int userId) {
        return libraryModel.getTransactionsByUser(userId);
    }

    public Map<String, String> getTransaction(int txnId) {
        return libraryModel.getTransaction(txnId);
    }
}
