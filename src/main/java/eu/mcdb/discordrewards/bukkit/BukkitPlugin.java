package eu.mcdb.discordrewards.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.plugin.java.JavaPlugin;
import org.spicord.Spicord;
import org.spicord.SpicordLoader;
import org.spicord.event.SpicordEvent;
import eu.mcdb.discordrewards.DiscordRewards;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.discordrewards.api.LinkingServiceImpl;
import eu.mcdb.discordrewards.command.LinkCommand;
import eu.mcdb.discordrewards.config.Config;
import org.spicord.embed.EmbedLoader;

public class BukkitPlugin extends JavaPlugin {

    private LinkManager linkManager;
    private Config config;
    private boolean firstRun = true;
    private LinkingServiceImpl ls;

    @Override
	public void onEnable() {
        saveDefaultConfig();
        saveResource("discord.yml", false);
        saveResource("rewards.yml", false);

        EmbedLoader embedLoader = extractEmbeds();

		File linked = new File(getDataFolder(), "linked.json");
		this.linkManager = new LinkManager(linked);

		this.config = new Config(getDataFolder(), getLogger());

		if (firstRun) {
		    firstRun = false;
            SpicordLoader.addStartupListener(s -> {
                s.getAddonManager().registerAddon(new DiscordRewards(linkManager, config));
                getServer().getPluginManager().registerEvents(new BukkitJoinListener(this, config.getRewards()), this);
            });
		}

		new LinkCommand(linkManager, config).register(this);

        long fiveMinInTicks = (60 * 5) * 20; // 6000 ticks
        getServer().getScheduler().runTaskTimer(this, () -> linkManager.save(), fiveMinInTicks, fiveMinInTicks);

        this.ls = new LinkingServiceImpl(linkManager);
        ls.register();
    }

    @Override
    public void onDisable() {
        linkManager.save();
        ls.unregister();
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        try {
            getDataFolder().mkdir();
            File out = new File(getDataFolder(), resourcePath);
            if (!out.exists() || replace) {
                InputStream in = getClass().getResourceAsStream("/" + resourcePath);
                Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EmbedLoader extractEmbeds() {
        try {
            return EmbedLoader.extractAndLoad(getFile(), new File(getDataFolder(), "embed"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
