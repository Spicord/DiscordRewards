package me.tini.discordrewards.sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.spicord.SpicordLoader;
import org.spicord.reflect.ReflectUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import me.tini.discordrewards.DiscordRewards;
import me.tini.discordrewards.DiscordRewardsPlugin;
import me.tini.discordrewards.linking.LinkManager;

@Plugin("discordrewards")
public class DiscordRewardsSponge implements DiscordRewardsPlugin {

    private DiscordRewards addon;
    private Path dataFolder;

    @Inject
    public DiscordRewardsSponge(@ConfigDir(sharedRoot = false) Path dataFolder) {
        this.dataFolder = dataFolder;

        SpicordLoader.addStartupListener(spicord -> {

            addon = new DiscordRewards(this);

            addon.initFields(spicord, getFile(), getDataFolder(), getLogger());

            spicord.getAddonManager().registerAddon(addon, false);

            LinkManager linkManager = addon.getLinkManager();

            PluginContainer spicordContainer = Sponge.pluginManager().fromInstance(this).get();

            Sponge.eventManager().registerListeners(
                spicordContainer,
                new SpongeJoinListener(
                    linkManager,
                    addon.getConfig().getRewards()
                )
            );

            TaskExecutorService executor = Sponge.asyncScheduler().executor(spicordContainer);

            executor.scheduleAtFixedRate(
                () -> linkManager.save(),
                5, 5, TimeUnit.MINUTES
            );

        });

    }

    @Override
    public DiscordRewards getAddon() {
        return addon;
    }

    @Override
    public String getVersion() {
        final String pluginId = DiscordRewardsSponge.class.getAnnotation(Plugin.class).value();

        return Sponge.pluginManager()
            .plugin(pluginId).get()
            .metadata()
            .version()
            .toString();
    }

    @Override
    public File getFile() {
        return ReflectUtils.getJarFile(DiscordRewardsSponge.class);
    }

    @Override
    public File getDataFolder() {
        return dataFolder.toFile();
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("discordrewards");
    }
}
