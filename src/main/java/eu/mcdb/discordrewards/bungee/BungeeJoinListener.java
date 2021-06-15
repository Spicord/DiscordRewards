package eu.mcdb.discordrewards.bungee;

import net.md_5.bungee.api.plugin.Listener;
import java.util.UUID;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.discordrewards.config.Rewards;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class BungeeJoinListener implements Listener {

    private LinkManager lm;
    private Rewards rewards;

    public BungeeJoinListener(LinkManager lm, Rewards rewards) {
        this.lm = lm;
        this.rewards = rewards;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String server = getServerName(player);

        if (rewards.isCached(uuid)) {
            if (server == null) {
                System.out.println("Can't find the player server, reward not given to: " + player.getName());
                return;
            }

            Account acc = lm.getAccount(uuid);

            //rewards.getCachedRewards(uuid).forEach(r -> r.give(acc));
            rewards.cleanCache(uuid);
        }
    }

    private String getServerName(ProxiedPlayer player) {
        try {
            return player.getServer().getInfo().getName().toString();
        } catch (Exception e) {}
        return null;
    }
}
