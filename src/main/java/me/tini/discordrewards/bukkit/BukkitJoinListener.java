package me.tini.discordrewards.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.ServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;

public class BukkitJoinListener extends ServerJoinListener implements Listener {

    public BukkitJoinListener(LinkManager linkManager, RewardManager rewards) {
        super(linkManager, rewards);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        super.onPlayerJoin(
            new UniversalPlayer(player.getName(), player.getUniqueId()),
            null, false
        );
    }
}
