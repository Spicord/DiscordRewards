package me.tini.discordrewards;

import java.util.UUID;

import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.config.RewardManager.Reward;
import me.tini.discordrewards.linking.LinkedAccount;
import me.tini.discordrewards.linking.LinkManager;

public abstract class AbstractServerJoinListener {

    private final DiscordRewards addon;

    public AbstractServerJoinListener(DiscordRewards addon) {
        this.addon = addon;
    }

    public void handlePlayerJoin(UniversalPlayer player, String serverName, boolean isProxy) {
        UUID uuid = player.getUniqueId();

        final LinkManager linkManager = addon.getLinkManager();
        final RewardManager rewards = addon.getConfig().getRewards();

        LinkedAccount account = linkManager.getAccount(uuid);

        if (account != null) {
            if (!player.getName().equals(account.getPlayerName())) {
                LinkedAccount newAccount = new LinkedAccount(
                    account.getDiscordId(),
                    player.getName(), // new name
                    uuid.toString(),
                    account.getMessageCount()
                );

                linkManager.getAccounts().remove(account.getDiscordId());
                linkManager.getAccounts().put(newAccount.getDiscordId(), newAccount);
                linkManager.save();

                account = newAccount;
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
