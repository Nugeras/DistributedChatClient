package ch.hsr.mapping.message;

import ch.hsr.domain.common.Username;
import ch.hsr.domain.groupmessage.GroupMessage;
import ch.hsr.domain.groupmessage.GroupMessageId;
import ch.hsr.domain.message.Message;
import ch.hsr.domain.message.MessageId;
import java.util.Optional;
import java.util.stream.Stream;

public interface MessageRepository {

    void send(Message message);

    void createMessage(Message message);

    // TODO add filter to not load all (paging)
    Stream<Message> getAllMessages(Username ownerUsername, Username otherUsername);

    Optional<Message> getMessage(MessageId messageId);

    Message receivedMessage();

    void send(GroupMessage groupMessage);

    void createGroupMessage(GroupMessage groupMessage);

    // TODO add filter to not load all (paging)
    Stream<GroupMessage> getAllGroupMessages(Username username);

    Optional<GroupMessage> getGroupMessage(GroupMessageId groupMessageId);

    GroupMessage receivedGroupMessage();
}
