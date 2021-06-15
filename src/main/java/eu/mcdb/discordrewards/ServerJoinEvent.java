package eu.mcdb.discordrewards;

import eu.mcdb.universal.player.UniversalPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServerJoinEvent {
    private final UniversalPlayer player;
    private final String serverName;
    private final boolean isProxy;
}
