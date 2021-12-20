package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.stream.Collectors;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
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
    public CheckBox multithreadedSolverCheckbox;

    @FXML
    public Button startCalculationsButton;

    boolean cityCountInputValid = true;
    boolean threadNumberInputValid = true;
    boolean specimenCountInputValid = true;
    boolean joiningPointInputValid = true;

    @FXML
    public void initialize() {
        CityNetworkSingleton singleton = CityNetworkSingleton.getInstance();
        NumericChangeListener joiningPointInputListener = new NumericChangeListener(joiningPointInput,
                1, singleton.getCityGridSize(), singleton::setJoiningPoint, change -> {
            joiningPointInputValid = change;
            validateStartButton();
        });
        cityCountInput.setText(singleton.getCityGridSize().toString());
        cityCountInput.textProperty().addListener(new NumericChangeListener(cityCountInput,
                5, 999, value -> {
            singleton.setCityGridSize(value);
            joiningPointInputListener.maximumValue = value;
        }, change -> {
            joiningPointInputListener.check();
            cityCountInputValid = change;
            validateStartButton();
        }));
        numberOfThreadsInput.setText(singleton.getTotalThreadAmount().toString());
        numberOfThreadsInput.textProperty().addListener(new NumericChangeListener(numberOfThreadsInput,
                2, 10, singleton::setTotalThreadAmount, change -> {
            threadNumberInputValid = change;
            validateStartButton();
        }));
        specimenCountInput.setText(singleton.getTotalSolutionsPerGeneration().toString());
        specimenCountInput.textProperty().addListener(new NumericChangeListener(specimenCountInput,
                5, 999, singleton::setTotalSolutionsPerGeneration, change -> {
            specimenCountInputValid = change;
            validateStartButton();
        }));
        joiningPointInput.setText(singleton.getJoiningPoint().toString());
        joiningPointInput.textProperty().addListener(joiningPointInputListener);
    }

    @FXML
    protected void toggleCalculations() {
        if(!calculating) {
            generation = 0;
            if(multithreadedSolverCheckbox.isSelected()) {
                calculator = CalculationModeSelector.MULTI_THREADED.createCalculator(this);
            } else {
                calculator = CalculationModeSelector.SINGLE_THREADED.createCalculator(this);
            }
            calculator.startCalculation();
        } else {
            calculator.stopCalculation();
        }
        calculating = !calculating;
    }

    private void validateStartButton() {
        startCalculationsButton.setDisable(!(joiningPointInputValid
                && specimenCountInputValid && cityCountInputValid
                && (threadNumberInputValid || !multithreadedSolverCheckbox.isSelected())));
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