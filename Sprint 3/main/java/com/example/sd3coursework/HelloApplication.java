package com.example.sd3coursework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the ResourceBundle based on the system's default locale
            Locale locale = Locale.getDefault();
            ResourceBundle bundle;

            try {
                bundle = ResourceBundle.getBundle("com.example.sd3coursework.i18n.messages", locale);
            } catch (Exception e) {
                // Fallback to English if the ResourceBundle for the selected locale is not found
                bundle = ResourceBundle.getBundle("com.example.sd3coursework.i18n.messages", Locale.ENGLISH);
                System.err.println("ResourceBundle not found for locale " + locale + ". Falling back to English.");
            }

            // Load the FXML file with the ResourceBundle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/sd3coursework/hello-view.fxml"), bundle);
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            // Apply the default CSS stylesheet
            URL cssUrl = getClass().getResource("/com/example/sd3coursework/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CSS file not found. Default styles will not be applied.");
            }

            // Set the stage title dynamically based on the ResourceBundle
            String appTitle = bundle.getString("app.title");
            stage.setTitle(appTitle != null ? appTitle : "Zip About - Rental Management System");

            // Set the scene to the stage
            stage.setScene(scene);

            // Show the stage
            stage.show();
        } catch (Exception e) {
            System.err.println("An error occurred while starting the application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
