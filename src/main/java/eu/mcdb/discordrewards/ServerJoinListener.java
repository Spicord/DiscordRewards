package eu.mcdb.discordrewards;

import java.util.UUID;

import eu.mcdb.discordrewards.config.RewardManager;
import eu.mcdb.universal.player.UniversalPlayer;

public class ServerJoinListener {

    private final LinkManager linkManager;
    private final RewardManager rewards;

    public ServerJoinListener(LinkManager linkManager, RewardManager rewards) {
        this.linkManager = linkManager;
        this.rewards = rewards;
    }

    public void onPlayerJoin(ServerJoinEvent event) {
        UniversalPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Account acc = linkManager.getAccount(uuid);

        if (acc != null) {
            if (!player.getName().equals(acc.getName())) {
                Account newAccount = new Account(
                        acc.getId(),
                        player.getName(), // new name
                        uuid.toString(),
                        acc.getMessageCount()
                );

                linkManager.getAccounts().remove(acc.getId());
                linkManager.getAccounts().put(newAccount.getId(), newAccount);
                linkManager.save();

                acc = newAccount;
            }

            if (rewards.isCached(uuid)) {
                if (event.isProxy() && event.getServerName() == null) {
                    System.out.println("Can't find the player server, reward not given to: " + player.getName());
                    return;
                }

                rewards.getCachedRewards(uuid).forEach(r -> rewards.give(r, player));
                rewards.cleanCache(uuid);
            }
        }
    }
}
