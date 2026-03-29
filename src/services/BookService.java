package services;

import models.Book;
import java.util.*;

public class BookService {
    private final Book bookModel;

    public BookService(Book bookModel) {
        this.bookModel = bookModel;
    }

    public Map<Integer, Map<String, String>> getAllBooks() {
        return bookModel.getBooks();
    }

    public Map<String, String> getBookById(int id) {
        return bookModel.searchBookByID(id);
    }

    public List<Map<String, String>> searchByName(String name) {
        return bookModel.searchBooksByName(name);
    }

    public List<Map<String, String>> searchByGenre(String genre) {
        return bookModel.searchBooksByGenre(genre);
    }

    public List<Map<String, String>> searchByAuthor(String author) {
        return bookModel.searchBooksByAuthor(author);
    }

    public boolean addBook(String name, String genre, String author, String year, int copies) {
        int newId = bookModel.getNextId();
        List<String> book = Arrays.asList(
            String.valueOf(newId), name, genre, author, year, String.valueOf(copies)
        );
        return bookModel.insertBook(book);
    }

    public boolean removeBook(int id) {
        return bookModel.deleteBookByID(id);
    }

    public boolean updateBook(int id, String name, String genre, String author, String year, int copies) {
        List<String> details = Arrays.asList(name, genre, author, year, String.valueOf(copies));
        return bookModel.updateBook(id, details);
    }

    public boolean isAvailable(int bookId) {
        Map<String, String> book = bookModel.searchBookByID(bookId);
        if (book == null) return false;
        int copies = Integer.parseInt(book.getOrDefault("copies", "0"));
        return copies > 0;
    }

    public boolean decrementCopies(int bookId) {
        return bookModel.decrementCopies(bookId);
    }

    public boolean incrementCopies(int bookId) {
        return bookModel.incrementCopies(bookId);
    }
}
