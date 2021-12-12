package pl.kkapalka.salesman;

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

        pl.kkapalka.salesman.logic.SalesmanSolutionPoolMultiThreaded pool = new pl.kkapalka.salesman.logic.SalesmanSolutionPoolMultiThreaded();
        pool.startCalculations();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.stopCalculations();

//        pl.kkapalka.salesman.logic.SalesmanSolutionPoolSingleThreaded pool = new pl.kkapalka.salesman.logic.SalesmanSolutionPoolSingleThreaded();
//        pool.startCalculations();
    }

    public static void main(String[] args) {
        launch();
    }
}