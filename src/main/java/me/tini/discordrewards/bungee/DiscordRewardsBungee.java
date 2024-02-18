package me.tini.discordrewards.bungee;

import java.util.concurrent.TimeUnit;

import org.spicord.SpicordLoader;

import me.tini.discordrewards.DiscordRewards;
import me.tini.discordrewards.DiscordRewardsPlugin;
import me.tini.discordrewards.linking.LinkManager;
import net.md_5.bungee.api.plugin.Plugin;

public class DiscordRewardsBungee extends Plugin implements DiscordRewardsPlugin {

    private DiscordRewards addon;

    @Override
    public void onEnable() {

        SpicordLoader.addStartupListener(spicord -> {

            addon = new DiscordRewards();

            addon.initFields(spicord, getFile(), getDataFolder(), getLogger());

            spicord.getAddonManager().registerAddon(addon, false);

            LinkManager linkManager = addon.getLinkManager();

            getProxy().getPluginManager().registerListener(this, new BungeeJoinListener(linkManager, addon.getConfig().getRewards()));

            getProxy().getScheduler().schedule(this, () -> linkManager.save(), 5, 5, TimeUnit.MINUTES);

        });

    }

    @Override
    public DiscordRewards getAddon() {
        return addon;
    }

    @Override
    public void onDisable() {
    }
}
