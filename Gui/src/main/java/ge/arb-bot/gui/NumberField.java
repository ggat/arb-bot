package ge.arb-bot.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Created by giga on 10/5/17.
 */
public class NumberField extends TextField {

    NumberField() {

        super();

        final NumberField self = this;

        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    self.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
}
