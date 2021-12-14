package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;

public class HelloController {

    boolean calculating = false;
    SalesmanSolutionCalculator calculator;

    @FXML
    protected void toggleCalculations() {
        if(!calculating) {
            calculator = CalculationModeSelector.MULTI_THREADED.createCalculator();
        }
        calculator.toggleCalculation();
        calculating = !calculating;
    }

}