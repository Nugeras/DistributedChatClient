package ch.hsr.view.chat.statusbox;

import ch.hsr.application.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StatusBoxController {

    private final UserService userService;

    @FXML
    private Label usernameLabel;

    public StatusBoxController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    protected void initialize() {
        usernameLabel.setText(userService.getSelf().toString());
    }
}