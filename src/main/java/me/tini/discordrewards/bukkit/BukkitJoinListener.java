package me.tini.discordrewards.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.AbstractServerJoinListener;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;

public class BukkitJoinListener extends AbstractServerJoinListener implements Listener {

    public BukkitJoinListener(LinkManager linkManager, RewardManager rewardManager) {
        super(linkManager, rewardManager);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        super.handlePlayerJoin(
            new UniversalPlayer(player.getName(), player.getUniqueId()),
            null, false
        );
    }
}
