package com.kinobase.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Точка входу у JavaFX-застосунок «Кінобаза».
 *
 * Запуск: {@code mvn javafx:run}
 *
 * @author Team Lead
 * @version 1.0
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("🎬 Кінобаза – Інформаційна система оцінювання фільмів");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
