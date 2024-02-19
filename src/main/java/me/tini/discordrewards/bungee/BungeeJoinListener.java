package me.tini.discordrewards.bungee;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.ServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener extends ServerJoinListener implements Listener {

    public BungeeJoinListener(LinkManager linkManager, RewardManager rewards) {
        super(linkManager, rewards);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();

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
