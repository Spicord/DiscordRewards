package eu.mcdb.discordrewards.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.discordrewards.ServerJoinEvent;
import eu.mcdb.discordrewards.ServerJoinListener;
import eu.mcdb.discordrewards.config.RewardManager;
import eu.mcdb.universal.player.UniversalPlayer;

public class BukkitJoinListener extends ServerJoinListener implements Listener {

    public BukkitJoinListener(LinkManager linkManager, RewardManager rewards) {
        super(linkManager, rewards);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        super.onPlayerJoin(new ServerJoinEvent(
            new UniversalPlayer(player.getName(), player.getUniqueId()),
            null, false
        ));
    }
}
