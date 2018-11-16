package ch.hsr.infrastructure.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity (name = "DbMessage")
@Table (name = "Messages")
@Data
@AllArgsConstructor
public class DbMessage {

    @Id
    @GeneratedValue
    @Column (name = "id")
    private Long id;
    @Column (name = "fromUsername")
    private String fromUsername;
    @Column (name = "toUsername")
    private String toUsername;
    @Column (name = "text")
    private String text;
    @Column (name = "timeStamp")
    private String timeStamp;
    @Column (name = "receivedMessage")
    private boolean received;
    @Column (name = "valid")
    private boolean valid;

    //needed by jpa
    public DbMessage() {

    }

    public static DbMessage newDbMessage(String fromUsername,
                                         String toUsername,
                                         String text,
                                         String timeStamp,
                                         boolean received,
                                         boolean valid) {
        return new DbMessage(
            null,
            fromUsername,
            toUsername,
            text,
            timeStamp,
            received,
            valid
        );
    }
}
