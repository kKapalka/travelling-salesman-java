package pl.kkapalka.salesman;

import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.CalculationModeSelector;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.delegates.SettingsControllerDelegate;
import pl.kkapalka.salesman.models.SalesmanSolution;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Border;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TabPane;

import java.util.List;

public class MainSalesmanController implements SalesmanCalculatorCallback, SalesmanControllerCallback {

    boolean calculating = false;
    SalesmanSolutionCalculator calculator;
    int generation = 0;
    CityNetworkSingleton singleton;
    List<SalesmanSolution> solutions;
    Border border;
    XYChart.Series<Number, Number> bestSpecimenSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> averageSpecimenSeries = new XYChart.Series<>();

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
    public HBox citiesHBox;
    @FXML
    public VBox citiesVBox;
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
    public LineChart<Number, Number> progressChart;
    @FXML
    public NumberAxis generationNumberAxis;
    @FXML
    public NumberAxis travelCostAxis;
    @FXML
    public TabPane mainTabPane;
    @FXML
    public Button startCalculationsButton;
    @FXML
    public Button cityGenerateButton;

    int previousTopColumnNumber = -1;
    int previousSideRowNumber = -1;

    SettingsControllerDelegate settingsControllerDelegate;

    @FXML
    public void initialize() {
        border = new Border(new javafx.scene.layout.BorderStroke(javafx.scene.paint.Color.BLACK, javafx.scene.layout.BorderStrokeStyle.SOLID, javafx.scene.layout.CornerRadii.EMPTY, javafx.scene.layout.BorderWidths.DEFAULT ));
        singleton = CityNetworkSingleton.getInstance();

        settingsControllerDelegate = new SettingsControllerDelegate(cityCountInput, numberOfThreadsInput, specimenCountInput,
                joiningPointInput, multithreadedSolverCheckbox, startCalculationsButton, this);

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
            mainTabPane.getSelectionModel().select(2);
            generationNumberAxis.setLowerBound(0);
            generationNumberAxis.setTickUnit(1000);
            generationNumberAxis.setUpperBound(30000);
            generationNumberAxis.setAutoRanging(false);
            generation = 1 - singleton.getChartRefreshRate();
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

    @Override
    public void onTransmitGraphData(Long minimumCost, Double averageCost) {
        javafx.application.Platform.runLater(() -> {
            generation+=singleton.getChartRefreshRate();
            if(bestSpecimenSeries.getData().size() > 30) {
                bestSpecimenSeries.getData().clear();
                averageSpecimenSeries.getData().clear();
                generationNumberAxis.setLowerBound(generation-1);
                generationNumberAxis.setUpperBound(generation + 29999);
                travelCostAxis.setAutoRanging(true);
            }
            averageSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation, averageCost / 10));
            bestSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation, minimumCost / 10));
        });
    }

    @Override
    public void onCollectLastGeneration(List<SalesmanSolution> solutions, int internalClock) {
        averageSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation + internalClock, solutions.stream().mapToLong(SalesmanSolution::getTotalTravelCost).average().getAsDouble() / 10));
        bestSpecimenSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(generation + internalClock, solutions.stream().min(SalesmanSolution::compareTo).get().getTotalTravelCost() / 10));
        this.solutions = solutions;
        generation += internalClock;
        modifyResultsPage();
    }

    private void toggleAllInputs() {
        settingsControllerDelegate.setDisabledInputs(calculating);
        cityGenerateButton.setDisable(calculating);
        symmetricPathCostCheckbox.setDisable(calculating);
        mainTabPane.setDisable(calculating);
    }

    private void setUpProgressChartPage() {
        generationNumberAxis.setTickUnit(1000);
        travelCostAxis.setForceZeroInRange(false);
        generationNumberAxis.setForceZeroInRange(false);
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
            Label label = new Label("Travel cost of specimen " + (i + 1) + ": " + HelperMethods.convertCostToString(Long.valueOf(solutions.get(i).getTotalTravelCost()).intValue()));
            label.setPrefHeight(25);
            label.setPrefWidth(resultsGrid.getWidth());

            label.setOnMousePressed(new TravelRouteMouseEventHandler(i) {
                @Override
                public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                    resultDetailsLabel.setText("Detailed travel route for specimen number " + (index + 1));
                    int[] travelRoute = solutions.get(index).getTravelRoute();
                    Integer[][] travelCosts = singleton.getCityTravelCostGrid();
                    resultDetailsGridView.getChildren().clear();
                    Label firstDetailLabel = new Label(HelperMethods.createCityName(travelRoute[0]) + "->");
                    firstDetailLabel.setPrefHeight(25);
                    resultDetailsGridView.add(firstDetailLabel, 0, 0);
                    int j = 1;
                    for (; j < travelCosts.length; j++) {
                        Label detailLabel = new Label(HelperMethods.createCityName(travelRoute[j]) + "(" + HelperMethods.convertCostToString(travelCosts[travelRoute[j - 1]][travelRoute[j]]) + ") ->");
                        detailLabel.setPrefHeight(25);
                        resultDetailsGridView.add(detailLabel, j % 3, j / 3);
                    }
                    Label returnDetailLabel = new Label(HelperMethods.createCityName(travelRoute[0]) + "(" + HelperMethods.convertCostToString(travelCosts[travelRoute[j-1]][travelRoute[0]]) + ")");
                    returnDetailLabel.setPrefHeight(25);
                    resultDetailsGridView.add(returnDetailLabel, j % 3, j / 3);
                }
            });
            resultsGrid.add(label, 0, i);
        }
    }

    private void setUpCitiesPage() {
        setUpNameGrids(0, 0);
        citiesGrid.getChildren().clear();
        citiesGrid.resize( 30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
        citiesGridScrollPane.resize( 30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());

        for(int i=0;i<singleton.getCityGridSize(); i++) {
            RowConstraints row = new RowConstraints(25);
            citiesGrid.getRowConstraints().add(row);
            ColumnConstraints col = new ColumnConstraints(30);
            citiesGrid.getColumnConstraints().add(col);
        }
        populateCitiesGrid();
        citiesGridScrollPane.setPannable(true);
        symmetricPathCostCheckbox.setSelected(true);
        symmetricPathCostCheckbox.selectedProperty().addListener((observable, oldV, newV) -> singleton.flipSymmetryConstraint());
        citiesGridScrollPane.vvalueProperty().addListener((ov, old_val, new_val) -> setUpNameGrids(citiesGridScrollPane.getVvalue() + 0.005,
                citiesGridScrollPane.getHvalue() + 0.005));
        citiesGridScrollPane.hvalueProperty().addListener((
                ov, old_val, new_val) -> setUpNameGrids(citiesGridScrollPane.getVvalue() + 0.005,
                        citiesGridScrollPane.getHvalue() + 0.005));
    }

    private void redrawCities() {
        citiesGrid.resize(30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
        citiesGridScrollPane.resize(30 * singleton.getCityGridSize(), 25 * singleton.getCityGridSize());
        citiesGridScrollPane.setHvalue(0);
        citiesGridScrollPane.setVvalue(0);
        citiesGrid.getChildren().clear();
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
        populateCitiesGrid();
        citiesGridScrollPane.layout();
    }


    private void populateCitiesGrid() {
        Integer[][] travelCostGrid = singleton.getCityTravelCostGrid();
        for(int i=0;i<singleton.getCityGridSize(); i++) {
            for (int j=0;j<singleton.getCityGridSize(); j++) {
                Label label = new Label(HelperMethods.convertCostToString(travelCostGrid[i][j]));
                label.setPrefWidth(28);
                label.setPrefHeight(23);
                label.setBorder(border);
                citiesGrid.add(label, i, j);
            }
        }
    }

    private void setUpNameGrids(double vValue, double hValue) {
        int sideRowNumber = (int) (vValue * (singleton.getCityGridSize() - 13));
        if(sideRowNumber != previousSideRowNumber) {
            previousSideRowNumber = sideRowNumber;
            sideCityNameGrid.getChildren().clear();
            for(int i=0;i<13; i++) {
                sideCityNameGrid.add(new Label(HelperMethods.createCityName(sideRowNumber + i)), 0, i);
            }
        }
        int topColumnNumber = (int) (hValue * (singleton.getCityGridSize() - 14));
        if(topColumnNumber != previousTopColumnNumber) {
            previousTopColumnNumber = topColumnNumber;
            topCityNameGrid.getChildren().clear();
            for(int i=0;i<14; i++) {
                topCityNameGrid.add(new Label(HelperMethods.createCityName(topColumnNumber + i)),  i, 0);
            }
        }

    }

    @Override
    public void onInvalidateCityData() {
        redrawCities();
        travelCostAxis.setUpperBound(singleton.getCityGridSize() * 47);
        travelCostAxis.setLowerBound(singleton.getCityGridSize() * 9);
        travelCostAxis.setAutoRanging(false);
    }
}
