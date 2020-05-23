package eu.mcdb.discordrewards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.plugin.java.JavaPlugin;
import eu.mcdb.discordrewards.command.LinkCommand;
import eu.mcdb.discordrewards.config.Config;
import eu.mcdb.spicord.Spicord;
import eu.mcdb.universal.MCDB;
import eu.mcdb.util.ZipExtractor;

public class BukkitPlugin extends JavaPlugin {

    private LinkManager linkManager;
    private Config config;
    private boolean firstRun = true;

    @Override
	public void onEnable() {
        extractEmbeds();
        saveDefaultConfig();
        saveResource("discord.yml", false);
        saveResource("rewards.yml", false);

		File linked = new File(getDataFolder(), "linked.json");
		this.linkManager = new LinkManager(linked);

		this.config = new Config(getDataFolder(), getLogger());

		if (firstRun) {
		    firstRun = false;
            Spicord.getInstance().onLoad(s -> {
                s.getAddonManager().registerAddon(new DiscordRewards(linkManager, config));
                getServer().getPluginManager().registerEvents(new BukkitJoinListener(this, config.getRewards()), this);
            });
		}

		MCDB.registerCommand(this, new LinkCommand(linkManager, config));

        long fiveMinInTicks = (60 * 5) * 20; // 6000 ticks
        getServer().getScheduler().runTaskTimer(this, () -> linkManager.save(), fiveMinInTicks, fiveMinInTicks);
    }

    @Override
    public void onDisable() {
        linkManager.save();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        try {
            getDataFolder().mkdir();
            File out = new File(getDataFolder(), resourcePath);
            if (!out.exists()) {
                out.createNewFile();
                InputStream is = getClass().getResourceAsStream("/" + resourcePath);
                byte[] buff = new byte[is.available()];
                is.read(buff);
                OutputStream os = new FileOutputStream(out);
                os.write(buff);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    private void extractEmbeds() {
        try {
            ZipExtractor ex = new ZipExtractor(getFile());
            ex.filter("embed\\/.*");
            ex.extract(getDataFolder());
            ex.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
