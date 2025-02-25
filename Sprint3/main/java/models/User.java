package models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final String role; // "User" or "Admin"
    private int loyaltyPoints;
    private List<String> bookingHistory;
    private String preferredLanguage; // Tracks the user's preferred language
    private String themePreference;   // Tracks the user's theme preference (e.g., "Light" or "Dark")

    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.loyaltyPoints = 0;
        this.bookingHistory = new ArrayList<>();
        this.preferredLanguage = "English"; // Default language
        this.themePreference = "Light";    // Default theme
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Check if the user is an admin
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    // Getter for loyalty points
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    // Add loyalty points
    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        } else {
            System.err.println("Invalid points: Points must be positive.");
        }
    }

    // Deduct loyalty points
    public void deductLoyaltyPoints(int points) {
        if (points > 0 && this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
        } else {
            System.err.println("Invalid points deduction: Points must be positive and not exceed current balance.");
        }
    }

    // Getter for booking history
    public List<String> getBookingHistory() {
        return new ArrayList<>(bookingHistory); // Return a copy to prevent modification
    }

    // Add a booking to the history
    public void addBooking(String itemName) {
        if (itemName != null && !itemName.isEmpty()) {
            this.bookingHistory.add(itemName);
        } else {
            System.err.println("Invalid item name: Cannot add null or empty booking.");
        }
    }

    // Clear booking history
    public void clearBookingHistory() {
        this.bookingHistory.clear();
    }

    // Getter for preferred language
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    // Setter for preferred language with validation
    public void setPreferredLanguage(String preferredLanguage) {
        if (preferredLanguage != null && (preferredLanguage.equalsIgnoreCase("English") || preferredLanguage.equalsIgnoreCase("French"))) {
            this.preferredLanguage = preferredLanguage;
        } else {
            System.err.println("Invalid language: Supported languages are English and French.");
        }
    }

    // Getter for theme preference
    public String getThemePreference() {
        return themePreference;
    }

    // Setter for theme preference with validation
    public void setThemePreference(String themePreference) {
        if (themePreference != null && (themePreference.equalsIgnoreCase("Light") || themePreference.equalsIgnoreCase("Dark"))) {
            this.themePreference = themePreference;
        } else {
            System.err.println("Invalid theme: Supported themes are Light and Dark.");
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", bookingHistory=" + bookingHistory +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", themePreference='" + themePreference + '\'' +
                '}';
    }
}
