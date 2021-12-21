package pl.kkapalka.salesman;

import javafx.fxml.FXML;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;
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
    javafx.scene.layout.Border border;
    javafx.scene.chart.XYChart.Series<Number, Number> bestSpecimenSeries = new javafx.scene.chart.XYChart.Series<>();
    javafx.scene.chart.XYChart.Series<Number, Number> averageSpecimenSeries = new javafx.scene.chart.XYChart.Series<>();

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
    public GridPane resultsGrid;
    @FXML
    public Label resultsLabel;
    @FXML
    public GridPane resultDetailsGridView;
    @FXML
    public Label resultDetailsLabel;
    @FXML
    public javafx.scene.chart.LineChart<Number, Number> progressChart;
    @FXML
    public javafx.scene.chart.NumberAxis generationNumberAxis;
    @FXML
    public javafx.scene.chart.NumberAxis travelCostAxis;
    @FXML
    public javafx.scene.control.TabPane mainTabPane;


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
        border = new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(javafx.scene.paint.Color.BLACK, javafx.scene.layout.BorderStrokeStyle.SOLID, javafx.scene.layout.CornerRadii.EMPTY, javafx.scene.layout.BorderWidths.DEFAULT ));
        labelReferences = new ArrayList<>();
        singleton = CityNetworkSingleton.getInstance();
        setUpSettingsPage();
        setUpCitiesPage();
        setUpProgressChartPage();
    }

    @FXML
    protected void toggleCalculations() {
        calculating = !calculating;
        if(calculating) {
            resultsGrid.getChildren().clear();
            resultDetailsGridView.getChildren().clear();
            resultsLabel.setText("");
            resultDetailsLabel.setText("");
            bestSpecimenSeries.getData().clear();
            averageSpecimenSeries.getData().clear();
            progressChart.setScaleX(1.0);
            progressChart.setScaleY(1.0);
            mainTabPane.getSelectionModel().select(2);
            mainTabPane.setDisable(true);
            generation = 1 - singleton.getChartRefreshRate();
            calculator = CalculationModeSelector.getCalculatorBasedOnCheckbox(multithreadedSolverCheckbox.isSelected()).createCalculator(this);
            calculator.startCalculation();
            startCalculationsButton.setText("Stop Calculations");
        } else {
            calculator.stopCalculation();
            mainTabPane.setDisable(false);
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
        javafx.application.Platform.runLater(() -> {
            generation+=singleton.getChartRefreshRate();
            if(bestSpecimenSeries.getData().size() > 30) {
                bestSpecimenSeries.getData().clear();
                averageSpecimenSeries.getData().clear();
                generationNumberAxis.setLowerBound(generation);
            }
            averageSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation, averageCost));
            bestSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation, minimumCost));

        });
    }

    @Override
    public void onCollectLastGeneration(List<SalesmanSolution> solutions, int internalClock) {
        averageSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation + internalClock, solutions.stream().mapToLong(SalesmanSolution::getTotalTravelCost).average().getAsDouble()));
        bestSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation + internalClock, solutions.stream().min(SalesmanSolution::compareTo).get().getTotalTravelCost()));
        this.solutions = solutions;
        generation += internalClock;
        modifyResultsPage();
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

    private void setUpProgressChartPage() {
        travelCostAxis.setUpperBound(singleton.getCityGridSize() * 470);
        travelCostAxis.setForceZeroInRange(false);
        generationNumberAxis.setForceZeroInRange(false);
        travelCostAxis.setLowerBound(singleton.getCityGridSize() * 100);
        bestSpecimenSeries.setName("Best specimen in generation");
        averageSpecimenSeries.setName("Average specimen in generation");
        progressChart.getData().addAll(bestSpecimenSeries, averageSpecimenSeries);
    }

    private void modifyResultsPage() {
        resultsLabel.setText("Results reached after "+generation+" generations:");
        resultsGrid.getChildren().clear();
        int resultsGridRowCount = resultsGrid.getRowCount();
        for(int i=solutions.size(); i < resultsGridRowCount; i++) {
            resultsGrid.getRowConstraints().remove(0);
        }
        for(int i= resultsGridRowCount; i < solutions.size(); i++) {
            RowConstraints row = new RowConstraints(25);
            resultsGrid.getRowConstraints().add(row);
        }
        int resultDetailsGridAmount = (int)Math.ceil((Integer.valueOf(singleton.getCityGridSize() + 1)).doubleValue() / 3.0);

        int resultDetailsGridRowCount = resultDetailsGridView.getRowCount();
        for(int i=resultDetailsGridAmount; i < resultDetailsGridRowCount; i++) {
            resultDetailsGridView.getRowConstraints().remove(0);
        }
        for(int i= resultDetailsGridRowCount; i < resultDetailsGridAmount; i++) {
            RowConstraints row = new RowConstraints(25);
            resultDetailsGridView.getRowConstraints().add(row);
        }
        resultsGrid.resize(resultsGrid.getWidth(), solutions.size() * 25);
        resultDetailsGridView.resize(resultDetailsGridView.getWidth(), resultDetailsGridAmount * 25);

        for(int i=0; i < solutions.size(); i++) {
            Label label = new Label("Travel cost of specimen " + (i + 1) + ": " + solutions.get(i).getTotalTravelCost());
            label.setPrefHeight(25);
            label.setPrefWidth(resultsGrid.getWidth());

            label.setOnMousePressed(new TravelRouteMouseEventHandler(i) {
                @Override
                public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                    resultDetailsLabel.setText("Detailed travel route for specimen number " + (index + 1));
                    int[] travelRoute = solutions.get(index).getTravelRoute();
                    Integer[][] travelCosts = singleton.getCityTravelCostGrid();
                    resultDetailsGridView.getChildren().clear();
                    Label firstDetailLabel = new Label(namePartsFirst[travelRoute[0] / namePartsSecond.length] + namePartsSecond[travelRoute[0] % namePartsSecond.length] + "->");
                    firstDetailLabel.setPrefHeight(25);
                    resultDetailsGridView.add(firstDetailLabel, 0, 0);
                    int j = 1;
                    for (; j < travelCosts.length; j++) {
                        Label detailLabel = new Label(namePartsFirst[travelRoute[j] / namePartsSecond.length] + namePartsSecond[travelRoute[j] % namePartsSecond.length] + "(" + travelCosts[travelRoute[j - 1]][travelRoute[j]] + ") ->");
                        detailLabel.setPrefHeight(25);
                        resultDetailsGridView.add(detailLabel, j % 3, j / 3);
                    }
                    Label returnDetailLabel = new Label(namePartsFirst[travelRoute[0] / namePartsSecond.length] + namePartsSecond[travelRoute[0] % namePartsSecond.length] + "(" + travelCosts[travelRoute[j-1]][travelRoute[0]] + ")");
                    returnDetailLabel.setPrefHeight(25);
                    resultDetailsGridView.add(returnDetailLabel, j % 3, j / 3);
                }
            });
            resultsGrid.add(label, 0, i);
        }
    }

    private void setUpCitiesPage() {
        setUpNameGrids(0, 0);
        Integer[][] travelCostGrid = singleton.getCityTravelCostGrid();
        citiesGrid.getChildren().clear();
        citiesGrid.resize( 30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
        citiesGridScrollPane.resize( 30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());

        for(int i=0;i<singleton.getCityGridSize(); i++) {
            RowConstraints row = new RowConstraints(25);
            citiesGrid.getRowConstraints().add(row);
            ColumnConstraints col = new ColumnConstraints(30);
            citiesGrid.getColumnConstraints().add(col);
        }
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            for (int j=0;j<singleton.getCityGridSize(); j++) {
                Label label = new Label(travelCostGrid[i][j].toString());
                label.setPrefWidth(28);
                label.setPrefHeight(23);
                label.setBorder(border);
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
            setUpNameGrids(citiesGridScrollPane.getVvalue() + 0.005,
                    citiesGridScrollPane.getHvalue() + 0.005);
                });
        citiesGridScrollPane.hvalueProperty().addListener((
                ov, old_val, new_val) -> {
            setUpNameGrids(citiesGridScrollPane.getVvalue() + 0.005,
                    citiesGridScrollPane.getHvalue() + 0.005);
        });
    }

    private void redrawCities() {
        citiesGrid.resize(30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
        citiesGridScrollPane.resize(30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
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
                label.setPrefWidth(28);
                label.setPrefHeight(23);
                label.setBorder(border);
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
            travelCostAxis.setUpperBound(value * 470);
            travelCostAxis.setLowerBound(value * 100);
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

    private void setUpNameGrids(double vValue, double hValue) {
        int sideRowNumber = (int) (vValue * (singleton.getCityGridSize() - 13));
        if(sideRowNumber != previousSideRowNumber) {
            previousSideRowNumber = sideRowNumber;
            sideCityNameGrid.getChildren().clear();
            for(int i=0;i<13; i++) {
                sideCityNameGrid.add(new Label(namePartsFirst[(sideRowNumber + i) / namePartsSecond.length] + namePartsSecond[(sideRowNumber + i) % namePartsSecond.length]), 0, i);
            }
        }
        int topColumnNumber = (int) (hValue * (singleton.getCityGridSize() - 14));
        if(topColumnNumber != previousTopColumnNumber) {
            previousTopColumnNumber = topColumnNumber;
            topCityNameGrid.getChildren().clear();
            for(int i=0;i<14; i++) {
                topCityNameGrid.add(new Label(namePartsFirst[(topColumnNumber + i) / namePartsSecond.length] + namePartsSecond[(topColumnNumber + i) % namePartsSecond.length]),  i, 0);
            }
        }

    }
}
