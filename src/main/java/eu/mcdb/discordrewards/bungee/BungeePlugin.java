package eu.mcdb.discordrewards.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import eu.mcdb.discordrewards.DiscordRewards;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.discordrewards.api.LinkingServiceImpl;
import eu.mcdb.discordrewards.command.LinkCommand;
import eu.mcdb.discordrewards.config.Config;
import org.spicord.Spicord;
import org.spicord.event.SpicordEvent;
import org.spicord.embed.EmbedLoader;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private LinkManager linkManager;
    private Config config;
    private LinkingServiceImpl ls;

    @Override
    public void onEnable() {
        saveResource("config.yml");
        saveResource("discord.yml");
        saveResource("rewards.yml");

        EmbedLoader loader = extractEmbeds();
        File linked = new File(getDataFolder(), "linked.json");
        this.linkManager = new LinkManager(linked);
        this.config = new Config(getDataFolder(), getLogger());

        getProxy().registerChannel("Spicord:DiscordRewards");

        Spicord.getInstance().addEventListener(SpicordEvent.SPICORD_LOADED, s -> {
            s.getAddonManager().registerAddon(new DiscordRewards(linkManager, config));
            getProxy().getPluginManager().registerListener(this, new BungeeJoinListener(linkManager, config.getRewards()));
        });

        new LinkCommand(linkManager, config).register(this);

        getProxy().getScheduler().schedule(this, () -> linkManager.save(), 5, 5, TimeUnit.MINUTES);

        this.ls = new LinkingServiceImpl(linkManager);
        ls.register();
    }

    @Override
    public void onDisable() {
        linkManager.save();
        ls.unregister();
    }

    private void saveResource(String name) {
        try {
            getDataFolder().mkdirs();
            File out = new File(getDataFolder(), name);
            if (!out.exists()) {
                InputStream in = getClass().getResourceAsStream("/" + name);
                Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EmbedLoader extractEmbeds() {
        try {
            return EmbedLoader.extractAndLoad(getFile(), new File(getDataFolder(), "embed"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
