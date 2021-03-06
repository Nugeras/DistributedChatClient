package ch.hsr.dcc.view.chat.messagebox;

import ch.hsr.dcc.domain.message.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class MessageEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEntry.class);

    private final Message message;

    @FXML
    @Getter
    private HBox self;

    @FXML
    private Label messageTextLabel;

    @FXML
    private Label messageTimeStampLabel;

    public MessageEntry(Message message) {
        this.message = message;

        //TODO use fx:controller="ch.hsr.dcc.view.chat.friendsbox.FriendEntry"
        // https://stackoverflow.com/questions/40047802/javafx-collection-binding-from-fxml-and-item-template
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/chat/messagebox/messageEntry.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            //TODO correct exception
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        //TODO show if receivedMessage
        messageTextLabel.setText(message.getText().toString());
        messageTimeStampLabel.setText(message.getTimeStamp().toString());
    }

}
