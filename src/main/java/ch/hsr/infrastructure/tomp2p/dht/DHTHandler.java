package ch.hsr.infrastructure.tomp2p.dht;

import ch.hsr.infrastructure.exception.DHTException;
import ch.hsr.infrastructure.tomp2p.PeerHolder;
import ch.hsr.infrastructure.tomp2p.PeerObject;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PutBuilder;
import net.tomp2p.p2p.JobScheduler;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class DHTHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DHTHandler.class);

    private final PeerHolder peerHolder;

    private final int ttl;
    private final int replicationInterval;

    private volatile Queue<PutBuilder> putBuilders = new LinkedList();

    public DHTHandler(PeerHolder peerHolder, int ttl, int replicationInterval) {
        this.peerHolder = peerHolder;
        this.ttl = ttl;
        this.replicationInterval = replicationInterval;
    }

    public void updateSelf() {
        PeerObject self = peerHolder.getSelf();
        addPeerObject(self.getUsername(), self, ttl);
    }

    private synchronized void addPeerObject(String key, PeerObject peerObject, int ttl) {
        try {
            addData(key, new Data(peerObject), ttl);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DHTException("String could not be converted to data");
        }
    }

    private void addData(String key, Data data, int ttl) {
        if (!key.isEmpty()) {
            if (ttl >= 0) {
                data.ttlSeconds(ttl);
            }

            PutBuilder putBuilder = peerHolder.getPeerDHT()
                .put(Number160.createHash(key))
                .data(data);

            putBuilders.add(putBuilder);
        } else {
            throw new DHTException("Key can't be empty");
        }
    }

    public Optional<PeerObject> getPeerObject(String username) {
        return getData(username)
            .map(this::dateToPeerObject);
    }

    private Optional<Data> getData(String key) {
        if (!key.isEmpty()) {
            FutureGet futureGet = peerHolder.getPeerDHT()
                .get(Number160.createHash(key))
                .start();

            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                return Optional.ofNullable(futureGet.data());
            } else {
                return Optional.empty();
            }
        } else {
            throw new DHTException("Key can't be empty");
        }
    }

    private PeerObject dateToPeerObject(Data data) {
        try {
            return (PeerObject) data.object();
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DHTException("Distributed hash table data could not be cast to peerObject");
        }
    }

    public synchronized void startReplication() {
        putBuilders.stream().map(putBuilder -> new JobScheduler(peerHolder.getPeer())
            .start(putBuilder, replicationInterval, 1)
            .shutdown()
        ).forEach(baseFuture -> {
            baseFuture.awaitUninterruptibly();
            if (baseFuture.isFailed()) {
                throw new DHTException("Distributed hash table data could not be replicated");
            }
        });

        // TODO nicer way to do this
        putBuilders = new LinkedList<>();
    }
}
