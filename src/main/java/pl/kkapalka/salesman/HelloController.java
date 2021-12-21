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
import javafx.scene.control.TableView;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.List;

public class HelloController implements SalesmanCalculatorCallback {

    boolean calculating = false;
    SalesmanSolutionCalculator calculator;
    int generation = 0;
    CityNetworkSingleton singleton;
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
    public javafx.scene.layout.GridPane citiesGrid;
    @FXML
    public javafx.scene.control.ScrollPane citiesGridScrollPane;

    @FXML
    public Button startCalculationsButton;

    boolean cityCountInputValid = true;
    boolean threadNumberInputValid = true;
    boolean specimenCountInputValid = true;
    boolean joiningPointInputValid = true;

    @FXML
    public void initialize() {
        singleton = CityNetworkSingleton.getInstance();
        setUpSettingsPage();
        setUpCitiesPage();
    }

    @FXML
    protected void toggleCalculations() {
        calculating = !calculating;
        if(calculating) {
            generation = 0;
            calculator = CalculationModeSelector.getCalculatorBasedOnCheckbox(multithreadedSolverCheckbox.isSelected()).createCalculator(this);
            calculator.startCalculation();
            startCalculationsButton.setText("Stop Calculations");
        } else {
            calculator.stopCalculation();
            startCalculationsButton.setText("Start Calculations");
        }
        toggleAllInputs();
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

    private void toggleAllInputs() {
        numberOfThreadsInput.setDisable(calculating);
        cityCountInput.setDisable(calculating);
        specimenCountInput.setDisable(calculating);
        joiningPointInput.setDisable(calculating);
        multithreadedSolverCheckbox.setDisable(calculating);
    }

    private void setUpCitiesPage() {
        Integer[][] travelCostGrid = singleton.getCityTravelCostGrid();
        citiesGrid.getChildren().clear();
        citiesGrid.resize(25 * singleton.getCityGridSize(), 30 * singleton.getCityGridSize());

        for(int i=0;i<singleton.getCityGridSize(); i++) {
            RowConstraints row = new RowConstraints(25);
            citiesGrid.getRowConstraints().add(row);
        }
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            ColumnConstraints col = new ColumnConstraints(30);
            citiesGrid.getColumnConstraints().add(col);
        }
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            for (int j=0;j<singleton.getCityGridSize(); j++) {
                citiesGrid.add(new javafx.scene.control.Label(travelCostGrid[i][j].toString()), i, j);
            }
        }
        citiesGridScrollPane.setPannable(true);
    }



    private void setUpSettingsPage() {
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
        multithreadedSolverCheckbox.selectedProperty().addListener((observable, oldV, newV) -> {
            validateStartButton();
            numberOfThreadsInput.setDisable(!newV);
        });
        multithreadedSolverCheckbox.setSelected(true);
    }
}
