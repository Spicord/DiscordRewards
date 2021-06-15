package eu.mcdb.discordrewards;

import eu.mcdb.discordrewards.config.Rewards;
import eu.mcdb.universal.player.UniversalPlayer;

public abstract class RewardManager {

    public abstract void give(Rewards.Reward reward, UniversalPlayer player);

    public abstract void cache(Rewards.Reward reward, UniversalPlayer player);
}
