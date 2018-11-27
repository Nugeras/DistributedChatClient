package ch.hsr.dsa.application;

import ch.hsr.dsa.mapping.friend.FriendRepository;
import ch.hsr.dsa.mapping.peer.PeerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class UserServiceConfiguration {

    @Bean
    public UserService userService(FriendRepository friendRepository, PeerRepository peerRepository) {
        return new UserService(friendRepository, peerRepository);
    }
}
