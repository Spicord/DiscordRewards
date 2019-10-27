package eu.mcdb.discordrewards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import eu.mcdb.discordrewards.command.LinkCommand;
import eu.mcdb.discordrewards.config.Config;
import eu.mcdb.spicord.Spicord;

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

		Map<String, FileConfiguration> cn = new HashMap<String, FileConfiguration>();
        cn.put("config", getConfig());
        cn.put("discord", getConfig("discord.yml"));
        cn.put("rewards", getConfig("rewards.yml"));

		this.config = new Config(this, cn);

		if (firstRun) {
		    firstRun = false;
            Spicord.getInstance().onLoad(s -> {
                s.getAddonManager().registerAddon(new DiscordRewards(this, config));
                getServer().getPluginManager().registerEvents(new JoinListener(this, config.getRewards()), this);
            });
		}
        getCommand("link").setExecutor(new LinkCommand(linkManager, config));

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

    private FileConfiguration getConfig(String name) {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
    }
    
    private void extractEmbeds() {
        try {
            JarFile jarFile = new JarFile(getFile());
            Enumeration<JarEntry> entries = jarFile.entries();

            File embedsFolder = new File(getDataFolder(), "embed");

            embedsFolder.mkdirs();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().startsWith("embed/") && entry.getName().endsWith(".json")) {
                    String embedName = entry.getName();
                    embedName = embedName.substring(embedName.lastIndexOf("/") + 1);

                    File file = new File(embedsFolder, embedName);

                    if (!file.exists()) {
                        file.createNewFile();
                        Files.copy(jarFile.getInputStream(entry), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            jarFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
