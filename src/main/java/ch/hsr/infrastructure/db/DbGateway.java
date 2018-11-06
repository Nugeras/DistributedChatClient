package ch.hsr.infrastructure.db;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface DbGateway {

    void createFriend(String username, String ownerUsername);

    Stream<DbFriend> getAllFriends(String ownerUsername);

    void createGroup(String name, Collection<String> members);

    Optional<DbGroup> getGroup(Long groupId);

    Stream<DbGroup> getAllGroups(String username);

    DbMessage createMessage(String fromUsername, String toUsername, String text, String timeStamp, boolean receive);

    DbMessage updateMessage(Long id, String fromUsername, String toUsername, String text, String timeStamp, boolean receive);

    Stream<DbMessage> getAllMessages(String ownerUsername, String otherUsername);

    // TODO change this to id
    void deleteMessage(DbMessage dbMessage);

    DbGroupMessage createGroupMessage(String fromUsername, Long toGroupId, String text, String timeStamp, Map<String, Boolean> received);

    DbGroupMessage updateGroupMessage(Long id, String fromUsername, Long toGroupId, String text, String timeStamp, Map<String, Boolean> received);

    Stream<DbGroupMessage> getAllGroupMessages(Long toGroupId);
}