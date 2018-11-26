package ch.hsr.dsa.infrastructure.exception;

public class CacheException extends IllegalArgumentException {

    private static final long serialVersionUID = 3026600374716018883L;

    public CacheException(String message) {
        super(message);
    }
}