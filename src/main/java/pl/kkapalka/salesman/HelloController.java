package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;

public class HelloController implements SalesmanCalculatorCallback {

    boolean calculating = false;
    SalesmanSolutionCalculator calculator;
    int generation = 0;

    @FXML
    protected void toggleCalculations() {
        if(!calculating) {
            generation = 0;
            calculator = CalculationModeSelector.SINGLE_THREADED.createCalculator(this);
        }
        calculator.toggleCalculation();
        calculating = !calculating;
    }

    @Override
    public void onTransmitGraphData(Long minimumCost, Double averageCost) {
        System.out.println("minimum cost - "+minimumCost);
        System.out.println("average cost - "+averageCost);
        generation++;
    }

    @Override
    public void onCollectLastGeneration(pl.kkapalka.salesman.models.SalesmanSolution[] solutions) {
        System.out.println("generation "+generation);
    }
}