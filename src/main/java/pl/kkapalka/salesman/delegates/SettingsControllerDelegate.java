package pl.kkapalka.salesman.delegates;

import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import pl.kkapalka.salesman.SalesmanControllerCallback;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import pl.kkapalka.salesman.NumericChangeListener;

public class SettingsControllerDelegate {


    public TextField cityCountInput;
    public TextField numberOfThreadsInput;
    public TextField specimenCountInput;
    public TextField joiningPointInput;
    public CheckBox multithreadedSolverCheckbox;
    public Button startCalculationsButton;

    boolean cityCountInputValid = true;
    boolean threadNumberInputValid = true;
    boolean specimenCountInputValid = true;
    boolean joiningPointInputValid = true;

    pl.kkapalka.salesman.SalesmanControllerCallback salesmanControllerCallback;

    public SettingsControllerDelegate(TextField cityCountInput, TextField numberOfThreadsInput,
                                      TextField specimenCountInput, TextField joiningPointInput,
                                      CheckBox multithreadedSolverCheckbox, Button startCalculationsButton,
                                      SalesmanControllerCallback salesmanControllerCallback) {
        this.cityCountInput = cityCountInput;
        this.numberOfThreadsInput = numberOfThreadsInput;
        this.specimenCountInput = specimenCountInput;
        this.joiningPointInput = joiningPointInput;
        this.multithreadedSolverCheckbox = multithreadedSolverCheckbox;
        this.startCalculationsButton = startCalculationsButton;
        this.salesmanControllerCallback = salesmanControllerCallback;
        setUp();
    }



    private void setUp() {
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
            salesmanControllerCallback.onInvalidateCityData();
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

    private void validateStartButton() {
        startCalculationsButton.setDisable(!(joiningPointInputValid
                && specimenCountInputValid && cityCountInputValid
                && (threadNumberInputValid || !multithreadedSolverCheckbox.isSelected())));
    }
    public void setDisabledInputs(boolean calculating) {
        numberOfThreadsInput.setDisable(calculating || !(multithreadedSolverCheckbox.isSelected()));
        cityCountInput.setDisable(calculating);
        specimenCountInput.setDisable(calculating);
        joiningPointInput.setDisable(calculating);
        multithreadedSolverCheckbox.setDisable(calculating);
    }
}
