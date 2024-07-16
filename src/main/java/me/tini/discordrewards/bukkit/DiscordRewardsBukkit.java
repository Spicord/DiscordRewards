package me.tini.discordrewards.bukkit;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.spicord.SpicordLoader;

import me.tini.discordrewards.DiscordRewards;
import me.tini.discordrewards.DiscordRewardsPlugin;
import me.tini.discordrewards.linking.LinkManager;

public class DiscordRewardsBukkit extends JavaPlugin implements DiscordRewardsPlugin {

    private boolean firstRun = true;

    private DiscordRewards addon;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholdersImpl placeholderImpl = new PlaceholdersImpl(this);
            placeholderImpl.registerNow();
        }

        if (firstRun) {
            firstRun = false;
            SpicordLoader.addStartupListener(spicord -> {

                addon = new DiscordRewards(this);

                addon.initFields(spicord, getFile(), getDataFolder(), getLogger());

                spicord.getAddonManager().registerAddon(addon, false);

                LinkManager linkManager = addon.getLinkManager();

                getServer().getPluginManager().registerEvents(new BukkitJoinListener(linkManager, addon.getConfig().getRewards()), this);

                long fiveMinInTicks = (60 * 5) * 20; // 6000 ticks
                getServer().getScheduler().runTaskTimer(this, () -> linkManager.save(), fiveMinInTicks, fiveMinInTicks);

            });
        }

    }

    @Override
    public DiscordRewards getAddon() {
        return addon;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    @Override
    public void onDisable() {
    }
}
