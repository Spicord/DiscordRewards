package eu.mcdb.discordrewards.bukkit;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.discordrewards.config.Rewards;

public class BukkitJoinListener implements Listener {

    private BukkitPlugin plugin;
    private Rewards rewards;

    public BukkitJoinListener(BukkitPlugin plugin, Rewards rewards) {
        this.plugin = plugin;
        this.rewards = rewards;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Account acc = plugin.getLinkManager().getAccount(uuid);

        if (acc != null) {
            if (!player.getName().equals(acc.getName())) {
                Account newAccount = new Account(
                        acc.getId(),
                        player.getName(), // new name
                        uuid.toString(),
                        acc.getMessageCount()
                );

                plugin.getLinkManager().getAccounts().remove(acc.getId());
                plugin.getLinkManager().getAccounts().put(newAccount.getId(), newAccount);
                plugin.getLinkManager().save();

                acc = newAccount;
            }

            if (rewards.isCached(uuid)) {
                rewards.getCachedRewards(uuid).forEach(r -> {
                    //TODO: give cached reward
                });
                rewards.cleanCache(uuid);
            }
        }
    }
}
