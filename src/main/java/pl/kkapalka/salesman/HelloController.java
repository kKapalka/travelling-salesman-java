package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
//        pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolMultiThreaded pool = new pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolMultiThreaded();
        pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolSingleThreaded pool = new pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolSingleThreaded();
        pool.startCalculations();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.stopCalculations();
    }
}