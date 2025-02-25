package utils;

import models.BookableItem;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private static BookingManager instance;
    private ObservableList<BookableItem> bookableItems;
    private List<User> users;

    private BookingManager() {
        this.bookableItems = FXCollections.observableArrayList();
        this.users = new ArrayList<>();
    }

    // Singleton pattern to ensure only one instance of BookingManager
    public static BookingManager getInstance() {
        if (instance == null) {
            instance = new BookingManager();
        }
        return instance;
    }

    // Getters
    public ObservableList<BookableItem> getBookableItems() {
        return FXCollections.unmodifiableObservableList(bookableItems); // Return an unmodifiable list for safety
    }

    public List<User> getUsers() {
        return new ArrayList<>(users); // Return a copy of the user list for safety
    }

    // Add a new user to the system
    public void addUser(User user) {
        if (user != null && users.stream().noneMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))) {
            users.add(user);
        } else {
            System.err.println("User already exists or is invalid.");
        }
    }

    // Add a new bookable item
    public void addBookableItem(BookableItem item) {
        if (item != null && bookableItems.stream().noneMatch(b -> b.getName().equalsIgnoreCase(item.getName()))) {
            bookableItems.add(item);
        } else {
            System.err.println("Item already exists or is invalid.");
        }
    }

    // Book an item for a user
    public boolean bookItem(String itemName, User user) {
        if (user == null) {
            System.err.println("User cannot be null.");
            return false;
        }

        for (BookableItem item : bookableItems) {
            if (item.getName().equalsIgnoreCase(itemName) && item.getStatus().equalsIgnoreCase("Available")) {
                item.setStatus("Rented");
                user.addBooking(itemName);
                user.addLoyaltyPoints(10);
                System.out.println("Item successfully booked: " + itemName);
                return true;
            }
        }
        System.err.println("Item not available or does not exist: " + itemName);
        return false;
    }

    // Release an item (make it available again)
    public boolean releaseItem(String itemName, User user) {
        if (user == null || itemName == null) {
            System.err.println("User or item name cannot be null.");
            return false;
        }

        for (BookableItem item : bookableItems) {
            if (item.getName().equalsIgnoreCase(itemName) && item.getStatus().equalsIgnoreCase("Rented")) {
                item.setStatus("Available");
                user.getBookingHistory().remove(itemName);
                System.out.println("Item successfully released: " + itemName);
                return true;
            }
        }
        System.err.println("Item is not rented or does not exist: " + itemName);
        return false;
    }

    // Login a user
    public User login(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    // Get booking history for a specific user
    public List<String> getBookingHistory(User user) {
        if (user == null) {
            System.err.println("User cannot be null.");
            return new ArrayList<>();
        }
        return new ArrayList<>(user.getBookingHistory());
    }

    // Get available items
    public List<BookableItem> getAvailableItems() {
        return bookableItems.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("Available"))
                .toList();
    }

    // Get rented items
    public List<BookableItem> getRentedItems() {
        return bookableItems.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("Rented"))
                .toList();
    }
}
