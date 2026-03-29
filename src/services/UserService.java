package services;

import models.User;
import java.util.*;

public class UserService {
    private final User userModel;
    private static final int MAX_BOOKS = 5;

    public UserService(User userModel) {
        this.userModel = userModel;
    }

    public Map<Integer, Map<String, String>> getAllUsers() {
        return userModel.getUsers();
    }

    public Map<String, String> getUserById(int id) {
        return userModel.searchUserById(id);
    }

    public List<Map<String, String>> searchByName(String name) {
        return userModel.searchUsersByName(name);
    }

    public boolean registerUser(String name, String className) {
        int newId = userModel.getNextId();
        List<String> user = Arrays.asList(
            String.valueOf(newId), name, className, "[]", "0"
        );
        return userModel.insertUser(user);
    }

    public boolean updateUser(int id, String name, String className) {
        Map<String, String> existing = userModel.searchUserById(id);
        if (existing == null) return false;
        List<String> updated = Arrays.asList(
            name, className,
            existing.getOrDefault("books", ""),
            existing.getOrDefault("due_fees", "0")
        );
        return userModel.updateStudent(updated, id);
    }

    public boolean canBorrow(int userId) {
        List<Integer> borrowed = userModel.getBorrowedBookIds(userId);
        return borrowed.size() < MAX_BOOKS;
    }

    public boolean addBookToUser(int userId, int bookId) {
        return userModel.addBookToUser(userId, bookId);
    }

    public boolean removeBookFromUser(int userId, int bookId) {
        return userModel.removeBookFromUser(userId, bookId);
    }

    public boolean addFee(int userId, double fee) {
        return userModel.addFee(userId, fee);
    }

    public boolean payFees(int userId, double amount) {
        return userModel.payFees(userId, amount);
    }

    public List<Integer> getBorrowedBooks(int userId) {
        return userModel.getBorrowedBookIds(userId);
    }

    public double getDueFees(int userId) {
        Map<String, String> user = userModel.searchUserById(userId);
        if (user == null) return 0;
        try {
            return Double.parseDouble(user.getOrDefault("due_fees", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
