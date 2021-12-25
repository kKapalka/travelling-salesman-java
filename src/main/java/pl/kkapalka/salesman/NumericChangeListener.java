package pl.kkapalka.salesman;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class NumericChangeListener implements ChangeListener<String> {

    TextField textField;
    NumericChangeCallback numericChangeCallback;
    ValueChangeCallback valueChangeCallback;
    int minimumValue;
    public int maximumValue;
    int currentValue = 0;

    public NumericChangeListener(TextField textField, int minimumValue, int maximumValue,
                                 NumericChangeCallback callback, ValueChangeCallback valueChangeCallback) {
        this.textField = textField;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.numericChangeCallback = callback;
        this.valueChangeCallback = valueChangeCallback;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        boolean succesfulChange = false;
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        } else {
            if(!newValue.isBlank()) {
                int value = Integer.parseInt(newValue);
                if(value >= minimumValue && value <= maximumValue) {
                    succesfulChange = true;
                    numericChangeCallback.onReturnNewValue(value);
                    currentValue = value;
                }
            }
        }
        valueChangeCallback.onAttemptedChange(succesfulChange);
    }

    public void check() {
        if(currentValue == 0) {
            currentValue = Integer.parseInt(textField.getText());
        }
        boolean validValue = currentValue >= minimumValue && currentValue <= maximumValue;
        valueChangeCallback.onAttemptedChange(validValue);
    }
}
