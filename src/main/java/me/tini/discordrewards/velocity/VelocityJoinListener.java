package me.tini.discordrewards.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.AbstractServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;

public class VelocityJoinListener extends AbstractServerJoinListener {

    public VelocityJoinListener(LinkManager linkManager, RewardManager rewardManager) {
        super(linkManager, rewardManager);
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();

        super.handlePlayerJoin(
            new UniversalPlayer(player.getUsername(), player.getUniqueId()),
            getServerName(player), true
        );
    }

    private String getServerName(Player player) {
        try {
            return player.getCurrentServer().get().getServerInfo().getName().toString();
        } catch (NullPointerException e) {}
        return null;
    }
}
