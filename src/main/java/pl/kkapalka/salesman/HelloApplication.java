package pl.kkapalka.salesman;

import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.Arrays;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        SalesmanSolution solution = new SalesmanSolution(false);
    }

    public static void main(String[] args) {
        launch();
    }
}