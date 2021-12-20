package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.stream.Collectors;
import javafx.scene.control.TextField;
import pl.kkapalka.salesman.NumericChangeListener;
import javafx.scene.control.Button;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.List;

public class HelloController implements SalesmanCalculatorCallback {

    boolean calculating = false;
    SalesmanSolutionCalculator calculator;
    int generation = 0;
    List<SalesmanSolution> solutions;

    @FXML
    protected TextField cityCountInput;
    @FXML
    protected TextField numberOfThreadsInput;
    @FXML
    protected TextField specimenCountInput;
    @FXML
    protected TextField joiningPointInput;

    @FXML
    public Button startCalculationsButton;

    boolean cityCountInputValid = true;
    boolean threadNumberInputValid = true;
    boolean specimenCountInputValid = true;
    boolean joiningPointInputValid = true;

    @FXML
    public void initialize() {
        CityNetworkSingleton singleton = CityNetworkSingleton.getInstance();
        cityCountInput.setText(singleton.getCityGridSize().toString());
        cityCountInput.textProperty().addListener(new NumericChangeListener(cityCountInput, text -> {
            boolean inputValid = false;
            if(!text.isBlank()) {
                int value = Integer.parseInt(text);
                if(value > 4 && value < 1000) {
                    inputValid = true;
                    singleton.setCityGridSize(value);
                }
            }
            cityCountInputValid = inputValid;
            validateStartButton();
        }));
        numberOfThreadsInput.setText(singleton.getTotalThreadAmount().toString());
        numberOfThreadsInput.textProperty().addListener(new NumericChangeListener(numberOfThreadsInput, text -> {
            boolean inputValid = false;
            if(!text.isBlank()) {
                int value = Integer.parseInt(text);
                if(value > 1 && value < 10) {
                    inputValid = true;
                    singleton.setTotalThreadAmount(value);
                }
            }
            threadNumberInputValid = inputValid;
            validateStartButton();
        }));
        specimenCountInput.setText(singleton.getTotalSolutionsPerGeneration().toString());
        specimenCountInput.textProperty().addListener(new NumericChangeListener(specimenCountInput, text -> {
            boolean inputValid = false;
            if(!text.isBlank()) {
                int value = Integer.parseInt(text);
                if(value > 4 && value < 1000) {
                    inputValid = true;
                    singleton.setTotalSolutionsPerGeneration(value);
                }
            }
            specimenCountInputValid = inputValid;
            validateStartButton();
        }));
        joiningPointInput.setText(singleton.getJoiningPoint().toString());
        joiningPointInput.textProperty().addListener(new NumericChangeListener(joiningPointInput, text -> {
            boolean inputValid = false;
            if(!text.isBlank()) {
                int value = Integer.parseInt(text);
                if(value > 0 && value < singleton.getCityGridSize()) {
                    inputValid = true;
                    singleton.setJoiningPoint(value);
                }
            }
            joiningPointInputValid = inputValid;
            validateStartButton();
        }));
    }

    @FXML
    protected void toggleCalculations() {
        if(!calculating) {
            generation = 0;
            calculator = CalculationModeSelector.MULTI_THREADED.createCalculator(this);
            calculator.startCalculation();
        } else {
            calculator.stopCalculation();
        }
        calculating = !calculating;
    }

    private void validateStartButton() {
        startCalculationsButton.setDisable(!(joiningPointInputValid && specimenCountInputValid && cityCountInputValid && threadNumberInputValid));
    }

    @Override
    public void onTransmitGraphData(Long minimumCost, Double averageCost) {
        System.out.println("minimum cost - "+minimumCost);
        System.out.println("average cost - "+averageCost);
        generation++;
    }

    @Override
    public void onCollectLastGeneration(List<SalesmanSolution> solutions) {
        System.out.println("generation "+generation);
        System.out.println(solutions.size());
        System.out.println(solutions.stream().map(SalesmanSolution::getTotalTravelCost).collect(Collectors.toList()));
    }
}