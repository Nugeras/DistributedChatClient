package ch.hsr.dcc.mapping.keystore;

import ch.hsr.dcc.domain.common.Username;
import ch.hsr.dcc.domain.group.Group;
import ch.hsr.dcc.domain.groupmessage.GroupMessage;
import ch.hsr.dcc.domain.keystore.PubKey;
import ch.hsr.dcc.domain.keystore.Sign;
import ch.hsr.dcc.domain.keystore.SignState;
import ch.hsr.dcc.domain.message.Message;
import ch.hsr.dcc.infrastructure.db.DbFriend;
import ch.hsr.dcc.infrastructure.tomp2p.dht.object.TomP2PGroupObject;
import ch.hsr.dcc.infrastructure.tomp2p.message.TomP2PGroupMessage;
import ch.hsr.dcc.infrastructure.tomp2p.message.TomP2PMessage;

public interface KeyStoreRepository {

    PubKey getPubKeyFromDb(Username username);

    Sign sign(Group group);

    Sign sign(TomP2PGroupObject tomP2PGroupObject);

    Sign sign(TomP2PMessage tomP2PMessage);

    Sign sign(TomP2PGroupMessage tomP2PGroupMessage);

    Sign sign(DbFriend dbFriend);

    // TODO remove
    SignState checkSignature(Username username, TomP2PMessage tomP2PMessage);

    SignState checkSignature(Username username, Message message);

    // TODO remove
    SignState checkSignature(Username username, TomP2PGroupMessage tomP2PGroupMessage);

    SignState checkSignature(Username username, GroupMessage groupMessage);

    SignState checkSignature(Username username, Group group);
}
