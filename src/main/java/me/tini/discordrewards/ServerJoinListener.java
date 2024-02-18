package me.tini.discordrewards;

import java.util.UUID;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.config.RewardManager.Reward;
import me.tini.discordrewards.linking.LinkedAccount;
import me.tini.discordrewards.linking.LinkManager;

public class ServerJoinListener {

    private final LinkManager linkManager;
    private final RewardManager rewards;

    public ServerJoinListener(LinkManager linkManager, RewardManager rewards) {
        this.linkManager = linkManager;
        this.rewards = rewards;
    }

    public void onPlayerJoin(UniversalPlayer player, String serverName, boolean isProxy) {
        UUID uuid = player.getUniqueId();

        LinkedAccount acc = linkManager.getAccount(uuid);

        if (acc != null) {
            if (!player.getName().equals(acc.getName())) {
                LinkedAccount newAccount = new LinkedAccount(
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
                if (isProxy && serverName == null) {
                    System.out.println("Can't find the player server, reward not given to: " + player.getName());
                    return;
                }

                for (Reward r : rewards.getCachedRewards(uuid).toArray(Reward[]::new)) {
                    rewards.give(r, player);
                }

                rewards.cleanCache(uuid);
            }
        }
    }
}
