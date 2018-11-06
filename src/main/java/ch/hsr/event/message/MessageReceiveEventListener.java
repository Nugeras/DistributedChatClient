package ch.hsr.event.message;

import ch.hsr.application.MessageService;
import ch.hsr.view.chat.messagebox.MessageBoxController;
import org.springframework.context.event.EventListener;

public class MessageReceiveEventListener {

    private final MessageBoxController messageBoxController;
    private final MessageService messageService;

    public MessageReceiveEventListener(MessageBoxController messageBoxController, MessageService messageService) {
        this.messageBoxController = messageBoxController;
        this.messageService = messageService;
    }

    @EventListener
    public void messageReceived(MessageReceivedEvent event) {
        // TODO probably better to do on same layer => controller
        messageService.messageReceived();
        messageBoxController.updateMessageListView();
    }
}
