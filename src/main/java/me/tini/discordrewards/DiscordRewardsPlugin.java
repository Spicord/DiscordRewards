package me.tini.discordrewards;

import org.spicord.plugin.PluginInterface;

public interface DiscordRewardsPlugin extends PluginInterface {
    DiscordRewards getAddon();

    String getVersion();
}
