package pl.kkapalka.salesman;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class NumericChangeListener implements ChangeListener<String> {

    TextField textField;
    TextChangeCallback callback;

    public NumericChangeListener(TextField textField, TextChangeCallback callback) {
        this.textField = textField;
        this.callback = callback;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            } else {
                callback.onTextChangeCallback(newValue);
            }
    }
}
