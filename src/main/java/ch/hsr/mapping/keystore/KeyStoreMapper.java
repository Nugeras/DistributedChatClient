package ch.hsr.mapping.keystore;

import ch.hsr.domain.common.Username;
import ch.hsr.domain.keystore.Sign;
import ch.hsr.domain.keystore.SignState;
import ch.hsr.infrastructure.db.DbGateway;
import ch.hsr.infrastructure.db.DbKeyPair;
import ch.hsr.infrastructure.tomp2p.TomP2P;
import ch.hsr.mapping.exception.NotFoundException;
import ch.hsr.mapping.exception.SignException;
import ch.hsr.mapping.peer.PeerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyStoreMapper implements KeyStoreRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStoreMapper.class);

    private final DbGateway dbGateway;
    private final TomP2P tomP2P;

    private final PeerRepository peerRepository;

    private final KeyPairGenerator keyPairGenerator;
    private final KeyFactory keyFactory;

    public KeyStoreMapper(DbGateway dbGateway,
                          TomP2P tomP2P,
                          PeerRepository peerRepository,
                          KeyPairGenerator keyPairGenerator,
                          KeyFactory keyFactory) {
        this.dbGateway = dbGateway;
        this.tomP2P = tomP2P;
        this.peerRepository = peerRepository;
        this.keyPairGenerator = keyPairGenerator;
        this.keyFactory = keyFactory;
    }

    @Override
    public Sign sign(int hashCode) {
        Username username = peerRepository.getSelf().getUsername();

        KeyPair keyPair = dbGateway.getKeyPair(username.toString())
            .map(this::dbKeyPairToKeyPair)
            .orElse(generateNewKeyPair(username));

        tomP2P.savePublicKey(username.toString(), encodeKey(keyPair.getPublic()));

        try {
            Signature signature = getSignature();
            signature.initSign(keyPair.getPrivate());
            signature.update(Integer.valueOf(hashCode).byteValue());

            return Sign.fromString(encodeBase64(signature.sign()));
        } catch (InvalidKeyException | SignatureException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SignException("Hash code can't be signed");
        }
    }

    private KeyPair generateNewKeyPair(Username username) {
        return keyPairGenerator.generateKeyPair();
    }

    private String encodeKey(Key key) {
        return encodeBase64(key.getEncoded());
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private Signature getSignature() {
        try {
            return Signature.getInstance("SHA1WithRSA");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Signature can't be initialized");
        }
    }

    private KeyPair dbKeyPairToKeyPair(DbKeyPair dbKeyPair) {
        return new KeyPair(
            decodePublicKey(dbKeyPair.getPublicKey()),
            decodePrivateKey(dbKeyPair.getPrivateKey())
        );
    }

    private PublicKey decodePublicKey(String key) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodeBase64(key));
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SignException("Public key can't be decoded");
        }
    }

    private byte[] decodeBase64(String string) {
        return Base64.getDecoder().decode(string);
    }

    private PrivateKey decodePrivateKey(String key) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decodeBase64(key));
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SignException("Private key can't be decoded");
        }
    }

    @Override
    public SignState CheckSignature(Username username, Sign sign, int hashCode) {
        return tomP2P.getPublicKey(username.toString())
            .map(this::decodePublicKey)
            .map(publicKey -> {
                try {
                    Signature signature = getSignature();
                    signature.verify(publicKey.getEncoded());
                    signature.update(Integer.valueOf(hashCode).byteValue());

                    if (signature.verify(decodeBase64(sign.toString()))) {
                        return SignState.VALID;
                    } else {
                        return SignState.INVALID;
                    }
                } catch (SignatureException e) {
                    LOGGER.error(e.getMessage(), e);
                    // TODO maybe just return unknown
                    throw new SignException("Can't check signature hash code");
                }
                // TODO maybe just return unknown
            }).orElseThrow(() -> new NotFoundException("Public key could not be found"));
    }
}
