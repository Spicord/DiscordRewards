package me.tini.discordrewards;

import org.spicord.bot.DiscordBot;

public interface DiscordRewardsExtension {

    void init(DiscordRewards addon);

    void preOnReady(DiscordBot bot);

    void postOnReady(DiscordBot bot);

    void onShutdown(DiscordBot bot);

    void onDisable();

}
