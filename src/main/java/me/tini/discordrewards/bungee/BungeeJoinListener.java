package me.tini.discordrewards.bungee;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.AbstractServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener extends AbstractServerJoinListener implements Listener {

    public BungeeJoinListener(LinkManager linkManager, RewardManager rewardManager) {
        super(linkManager, rewardManager);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        super.handlePlayerJoin(
            new UniversalPlayer(player.getName(), player.getUniqueId()),
            getServerName(player), true
        );
    }

    private String getServerName(ProxiedPlayer player) {
        try {
            return player.getServer().getInfo().getName().toString();
        } catch (NullPointerException e) {}
        return null;
    }
}
