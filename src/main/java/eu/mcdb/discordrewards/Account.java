package eu.mcdb.discordrewards;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Account {

    @Getter
    private Long id;
    @Getter
    private String name;

    private String uuid;
    private int message_count;

    public UUID getUniqueId() {
        return UUID.fromString(uuid);
    }

    public int getMessageCount() {
        return message_count;
    }

    public void setMessageCount(int count) {
        this.message_count = count;
    }
}
