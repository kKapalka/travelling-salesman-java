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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;

import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.List;

public class HelloController implements SalesmanCalculatorCallback {

    boolean calculating = false;
    String[] namePartsFirst = new String[]{"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    String[] namePartsSecond = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
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
    public CheckBox symmetricPathCostCheckbox;
    @FXML
    public GridPane citiesGrid;
    @FXML
    public ScrollPane citiesGridScrollPane;
    @FXML
    public javafx.scene.layout.HBox citiesHBox;
    @FXML
    public javafx.scene.layout.VBox citiesVBox;
    @FXML
    public GridPane topCityNameGrid;
    @FXML
    public GridPane sideCityNameGrid;

    @FXML
    public Button startCalculationsButton;
    @FXML
    public Button cityGenerateButton;

    int previousTopColumnNumber = -1;
    int previousSideRowNumber = -1;


    ArrayList<Label> labelReferences;
    boolean cityCountInputValid = true;
    boolean threadNumberInputValid = true;
    boolean specimenCountInputValid = true;
    boolean joiningPointInputValid = true;

    @FXML
    public void initialize() {
        labelReferences = new ArrayList<>();
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

    @FXML
    protected void generateNewCityGrid() {
        singleton.generateNewGrid();
        redrawCities();
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
        cityGenerateButton.setDisable(calculating);
        symmetricPathCostCheckbox.setDisable(calculating);
    }

    private void setUpCitiesPage() {
        setUpNameGrids(0, 0);
        Integer[][] travelCostGrid = singleton.getCityTravelCostGrid();
        citiesGrid.getChildren().clear();
        citiesGrid.resize(25 * singleton.getCityGridSize(), 30 * singleton.getCityGridSize());
        citiesGridScrollPane.resize(25 * singleton.getCityGridSize(), 30 * singleton.getCityGridSize());

        for(int i=0;i<singleton.getCityGridSize(); i++) {
            RowConstraints row = new RowConstraints(25);
            citiesGrid.getRowConstraints().add(row);
            ColumnConstraints col = new ColumnConstraints(30);
            citiesGrid.getColumnConstraints().add(col);
        }
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            for (int j=0;j<singleton.getCityGridSize(); j++) {
                Label label = new Label(travelCostGrid[i][j].toString());
                labelReferences.add(label);
                citiesGrid.add(label, i, j);
            }
        }
        citiesGridScrollPane.setPannable(true);
        symmetricPathCostCheckbox.setSelected(true);
        symmetricPathCostCheckbox.selectedProperty().addListener((observable, oldV, newV) -> {
            singleton.flipSymmetryConstraint();
        });
        citiesGridScrollPane.vvalueProperty().addListener((ov, old_val, new_val) -> {
            setUpNameGrids((int)(citiesGridScrollPane.getVvalue() * 30 * singleton.getCityGridSize()),
                    (int)(citiesGridScrollPane.getHvalue() * 25 * singleton.getCityGridSize()));
                });
        citiesGridScrollPane.hvalueProperty().addListener((
                ov, old_val, new_val) -> {
            setUpNameGrids((int)(citiesGridScrollPane.getVvalue() * 30 * singleton.getCityGridSize()),
                    (int)(citiesGridScrollPane.getHvalue() * 25 * singleton.getCityGridSize()));
        });
    }

    private void redrawCities() {
        citiesGrid.resize(25 * singleton.getCityGridSize(), 30 * singleton.getCityGridSize());
        citiesGridScrollPane.resize(25 * singleton.getCityGridSize(), 30 * singleton.getCityGridSize());
        Integer[][] travelCostGrid = singleton.getCityTravelCostGrid();
        citiesGridScrollPane.setHvalue(0);
        citiesGridScrollPane.setVvalue(0);
        for(Label label: labelReferences) {
            citiesGrid.getChildren().remove(label);
        }
        int currentColConstraintSize = citiesGrid.getColumnCount();
        for(int i=singleton.getCityGridSize(); i<currentColConstraintSize; i++) {
            citiesGrid.getColumnConstraints().remove(0);
            citiesGrid.getRowConstraints().remove(0);
        }
        for(int i=currentColConstraintSize; i<singleton.getCityGridSize(); i++) {
            RowConstraints row = new RowConstraints(25);
            citiesGrid.getRowConstraints().add(row);
            ColumnConstraints col = new ColumnConstraints(30);
            citiesGrid.getColumnConstraints().add(col);
        }
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            for (int j=0;j<singleton.getCityGridSize(); j++) {
                Label label = new Label(travelCostGrid[i][j].toString());
                labelReferences.add(label);
                citiesGrid.add(label, i, j);
            }
        }
        citiesGridScrollPane.layout();
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
            redrawCities();
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

    private void setUpNameGrids(int vValue, int hValue) {
        int topColumnNumber = vValue / 30;
        int sideRowNumber = hValue / 25;
        if(topColumnNumber != previousTopColumnNumber) {
            previousTopColumnNumber = topColumnNumber;
            topCityNameGrid.getChildren().clear();
            for(int i=0;i<14; i++) {
                topCityNameGrid.add(new Label(namePartsFirst[(topColumnNumber + i) / namePartsSecond.length] + namePartsSecond[(topColumnNumber + i) % namePartsSecond.length]),  i, 0);
            }
        }
        if(sideRowNumber != previousSideRowNumber) {
            previousSideRowNumber = sideRowNumber;
            sideCityNameGrid.getChildren().clear();
            for(int i=0;i<13; i++) {
                sideCityNameGrid.add(new Label(namePartsFirst[(sideRowNumber + i) / namePartsSecond.length] + namePartsSecond[(sideRowNumber + i) % namePartsSecond.length]), 0, i);
            }
        }

    }
}
