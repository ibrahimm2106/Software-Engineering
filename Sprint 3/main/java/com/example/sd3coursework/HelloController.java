package com.example.sd3coursework;
import java.net.URL;
import models.BookableItem;
import models.User;
import utils.BookingManager;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelloController {

    @FXML
    private ComboBox<String> vehicleComboBox;

    @FXML
    private ComboBox<String> equipmentComboBox;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button rentButton;

    @FXML
    private Button filterButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button themeToggleButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<BookableItem> tableView;

    @FXML
    private TableColumn<BookableItem, String> itemColumn;

    @FXML
    private TableColumn<BookableItem, String> statusColumn;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TabPane tabPane;

    @FXML
    private PieChart bookingChart;

    private BookingManager bookingManager;
    private User currentUser;

    private boolean darkModeEnabled = false;

    @FXML
    public void initialize() {
        bookingManager = BookingManager.getInstance();

        // Initialize test users and items
        bookingManager.addUser(new User("Admin", "Admin"));
        bookingManager.addUser(new User("User1", "User"));
        currentUser = null; // No user logged in initially

        bookingManager.addBookableItem(new BookableItem("Mountain Bike", "Available"));
        bookingManager.addBookableItem(new BookableItem("Electric Scooter", "Available"));
        bookingManager.addBookableItem(new BookableItem("Helmet", "Available"));
        bookingManager.addBookableItem(new BookableItem("Knee Pads", "Available"));

        // Populate language ComboBox
        if (languageComboBox != null) {
            languageComboBox.getItems().addAll("English", "French");
            languageComboBox.setOnAction(event -> handleLanguageChange());
        } else {
            System.err.println("languageComboBox is not initialized.");
        }

        // Bind TableView columns
        itemColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        // Configure keyboard accessibility
        configureKeyboardShortcuts();

        // Populate ComboBoxes and TableView
        updateView();

        // Notify the user
        notifyObservers("Welcome to Zip About!");
    }

    private void configureKeyboardShortcuts() {
        if (tabPane != null) {
            tabPane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.TAB) {
                    tabPane.getSelectionModel().selectNext();
                }
            });
        }

        if (searchField != null) {
            searchField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleSearchAction();
                }
            });
        }
    }

    @FXML
    private void handleThemeToggle() {
        if (tabPane == null || tabPane.getScene() == null) {
            System.err.println("TabPane or Scene is not properly initialized. Cannot toggle theme.");
            return;
        }

        Scene scene = tabPane.getScene();
        darkModeEnabled = !darkModeEnabled;
        scene.getStylesheets().clear();
        String theme = darkModeEnabled ? "dark-theme.css" : "style.css";

        try {
            // Attempt to load the stylesheet
            URL themeUrl = getClass().getResource("/com/example/sd3coursework/" + theme);
            if (themeUrl != null) {
                scene.getStylesheets().add(themeUrl.toExternalForm());
                notifyObservers("Theme switched to " + (darkModeEnabled ? "Dark" : "Light") + " Mode");
            } else {
                throw new NullPointerException("Theme file not found: " + theme);
            }
        } catch (Exception e) {
            System.err.println("Failed to load theme: " + theme);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLanguageChange() {
        if (languageComboBox == null) return;

        String selectedLanguage = languageComboBox.getValue();
        if (selectedLanguage == null) return;

        Locale locale = selectedLanguage.equals("French") ? new Locale("fr", "FR") : Locale.ENGLISH;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.example.sd3coursework.i18n.messages", locale);
            // Update UI labels dynamically
            statusLabel.setText(bundle.getString("status.welcome"));
            rentButton.setText(bundle.getString("button.rent"));
            searchButton.setText(bundle.getString("button.search"));
            filterButton.setText(bundle.getString("button.filter"));
            notifyObservers("Language changed to " + selectedLanguage);
        } catch (Exception e) {
            System.err.println("Error loading language bundle for locale: " + locale);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginAction() {
        TextInputDialog loginDialog = new TextInputDialog();
        loginDialog.setTitle("Login");
        loginDialog.setHeaderText("Enter your username:");
        loginDialog.setContentText("Username:");

        loginDialog.showAndWait().ifPresent(username -> {
            User user = bookingManager.login(username);
            if (user != null) {
                currentUser = user;
                notifyObservers("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
                if (currentUser.getRole().equals("Admin")) {
                    notifyObservers("Admin privileges enabled.");
                }
            } else {
                showAlert("Error", "User not found.");
            }
        });
    }

    @FXML
    private void handleRentAction() {
        if (currentUser == null) {
            showAlert("Error", "Please log in to rent an item.");
            return;
        }

        String selectedVehicle = vehicleComboBox.getValue();
        String selectedEquipment = equipmentComboBox.getValue();

        if (selectedVehicle == null && selectedEquipment == null) {
            showAlert("Error", "Please select a vehicle or equipment to rent.");
            return;
        }

        if (selectedVehicle != null) {
            boolean success = bookingManager.bookItem(selectedVehicle, currentUser);
            if (success) {
                notifyObservers("You rented: " + selectedVehicle);
            } else {
                showAlert("Error", "Vehicle is already rented.");
            }
        }

        if (selectedEquipment != null) {
            boolean success = bookingManager.bookItem(selectedEquipment, currentUser);
            if (success) {
                notifyObservers("You rented: " + selectedEquipment);
            } else {
                showAlert("Error", "Equipment is already rented.");
            }
        }

        updateView();
        progressBar.setProgress((double) currentUser.getLoyaltyPoints() / 100);
    }

    @FXML
    private void handleSearchAction() {
        if (searchField == null) return;

        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            showAlert("Error", "Search field is empty.");
            return;
        }

        List<BookableItem> searchResults = bookingManager.getBookableItems().stream()
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        if (!searchResults.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList(searchResults));
            notifyObservers("Search results for: " + query);
        } else {
            notifyObservers("No items found for query: " + query);
        }
    }

    @FXML
    private void handleFilterAction() {
        String filterCriteria = "Available";

        List<BookableItem> filteredItems = bookingManager.getBookableItems().stream()
                .filter(item -> item.getStatus().equalsIgnoreCase(filterCriteria))
                .collect(Collectors.toList());

        if (!filteredItems.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList(filteredItems));
            notifyObservers("Filtered items by status: " + filterCriteria);
        } else {
            notifyObservers("No items match the filter criteria.");
        }
    }

    private void updateView() {
        tableView.setItems(bookingManager.getBookableItems());
        if (vehicleComboBox != null) vehicleComboBox.getItems().clear();
        if (equipmentComboBox != null) equipmentComboBox.getItems().clear();

        for (BookableItem item : bookingManager.getBookableItems()) {
            if (item.getStatus().equalsIgnoreCase("Available")) {
                if (item.getName().equalsIgnoreCase("Mountain Bike") || item.getName().equalsIgnoreCase("Electric Scooter")) {
                    if (vehicleComboBox != null) vehicleComboBox.getItems().add(item.getName());
                } else {
                    if (equipmentComboBox != null) equipmentComboBox.getItems().add(item.getName());
                }
            }
        }

        updateChart();
    }

    private void updateChart() {
        int availableCount = (int) bookingManager.getBookableItems().stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("Available"))
                .count();

        int bookedCount = bookingManager.getBookableItems().size() - availableCount;

        bookingChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Available", availableCount),
                new PieChart.Data("Booked", bookedCount)
        ));
    }

    private void notifyObservers(String message) {
        statusLabel.setText(message);

        FadeTransition fade = new FadeTransition(Duration.seconds(2), statusLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
