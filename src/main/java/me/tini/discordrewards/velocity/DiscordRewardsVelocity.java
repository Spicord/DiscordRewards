package me.tini.discordrewards.velocity;

import java.util.concurrent.TimeUnit;

import org.spicord.SpicordLoader;
import org.spicord.plugin.VelocityPlugin;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import me.tini.discordrewards.DiscordRewards;
import me.tini.discordrewards.DiscordRewardsPlugin;
import me.tini.discordrewards.linking.LinkManager;

@Plugin(
    id = "discordrewards",
    name = "DiscordRewards",
    authors = { "Tini" },
    version = "2.0.0",
    dependencies = {
        @Dependency(id = "spicord", optional = false)
    }
)
public class DiscordRewardsVelocity extends VelocityPlugin implements DiscordRewardsPlugin {

    private DiscordRewards addon;

    @Inject
    public DiscordRewardsVelocity(ProxyServer server) {
        super(server);
    }

    @Override
    public void onEnable() {

        SpicordLoader.addStartupListener(spicord -> {

            addon = new DiscordRewards(this);

            addon.initFields(spicord, getFile(), getDataFolder(), getLogger());

            spicord.getAddonManager().registerAddon(addon, false);

            LinkManager linkManager = addon.getLinkManager();

            getEventManager().register(
                this,
                new VelocityJoinListener(
                    linkManager,
                    addon.getConfig().getRewards()
                )
            );

            getProxyServer()
                .getScheduler()
                .buildTask(this, () -> linkManager.save())
                .delay(5, TimeUnit.MINUTES)
                .repeat(5, TimeUnit.MINUTES)
                .schedule()
            ;

        });

    }

    @Override
    public DiscordRewards getAddon() {
        return addon;
    }

    @Override
    public String getVersion() {
        return DiscordRewardsVelocity.class.getAnnotation(Plugin.class).version();
    }
}
