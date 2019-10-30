package eu.mcdb.discordrewards;

import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import eu.mcdb.discordrewards.config.Config.Rewards;

public class JoinListener implements Listener {

    private BukkitPlugin plugin;
    private Rewards rewards;

    public JoinListener(BukkitPlugin plugin, Rewards rewards) {
        this.plugin = plugin;
        this.rewards = rewards;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (rewards.isCached(uuid)) {
            Account acc = plugin.getLinkManager().getAccountByUniqueId(uuid);
            rewards.getCachedRewards(uuid).forEach(r -> r.give(acc));
            rewards.cleanCache(uuid);
        }
    }
}
