package me.tini.discordrewards.linking;

import org.spicord.api.services.linking.LinkData;

public class LinkedAccount extends LinkData {

    private int message_count;

    public LinkedAccount(Long id, String name, String uuid, int message_count) {
        super(id, name, uuid);
        this.message_count = message_count;
    }

    public int getMessageCount() {
        return message_count;
    }

    public void setMessageCount(int count) {
        this.message_count = count;
    }
}
