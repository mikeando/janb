package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class Controller {
    @FXML
    protected PasswordField passwordField;

    @FXML
    protected Text actiontarget;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent actionEvent) {
        actiontarget.setText("Sign in button pressed");
    }
}
