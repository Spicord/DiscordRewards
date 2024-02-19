package me.tini.discordrewards.sponge;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.ServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;

public class SpongeJoinListener extends ServerJoinListener {

    public SpongeJoinListener(LinkManager linkManager, RewardManager rewards) {
        super(linkManager, rewards);
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join e) {
        ServerPlayer player = e.player();

        super.handlePlayerJoin(
            new UniversalPlayer(player.name(), player.uniqueId()),
            null, false
        );
    }
}
