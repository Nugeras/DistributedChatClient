package ch.hsr.dcc.application;

import ch.hsr.dcc.domain.common.Username;
import ch.hsr.dcc.domain.group.Group;
import ch.hsr.dcc.domain.group.GroupName;
import ch.hsr.dcc.domain.peer.Peer;
import ch.hsr.dcc.mapping.group.GroupRepository;
import ch.hsr.dcc.mapping.peer.PeerRepository;
import org.springframework.scheduling.annotation.Async;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupService {

    private final GroupRepository groupRepository;
    private final PeerRepository peerRepository;

    public GroupService(GroupRepository groupRepository, PeerRepository peerRepository) {
        this.groupRepository = groupRepository;
        this.peerRepository = peerRepository;
    }

    //TODO notify all peers that they are now in a group
    @Async
    public void addGroup(GroupName groupName, Set<Username> usernames) {
        Set<Peer> members = usernames.stream().map(peerRepository::get)
            .collect(Collectors.toSet());

        members.add(peerRepository.getSelf());

        groupRepository.create(Group.newGroup(groupName, members));
    }

    public Stream<Group> getAllGroups() {
        Username username = peerRepository.getSelf().getUsername();

        return groupRepository.getAll(username);
    }
}
