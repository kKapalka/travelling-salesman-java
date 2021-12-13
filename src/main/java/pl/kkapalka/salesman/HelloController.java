package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
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
}