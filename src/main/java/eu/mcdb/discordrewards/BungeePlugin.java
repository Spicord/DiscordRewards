package eu.mcdb.discordrewards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import eu.mcdb.discordrewards.command.LinkCommand;
import eu.mcdb.discordrewards.config.Config;
import eu.mcdb.spicord.Spicord;
import eu.mcdb.universal.MCDB;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private LinkManager linkManager;
    private Config config;

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        saveResource("discord.yml", false);
        saveResource("rewards.yml", false);

        File linked = new File(getDataFolder(), "linked.json");
        this.linkManager = new LinkManager(linked);
        this.config = new Config(getDataFolder(), getLogger());

        getProxy().registerChannel("Spicord:DiscordRewards");

        Spicord.getInstance().onLoad(s -> {
            s.getAddonManager().registerAddon(new DiscordRewards(linkManager, config));
            getProxy().getPluginManager().registerListener(this, new BungeeJoinListener(linkManager, config.getRewards()));
        });

        MCDB.registerCommand(this, new LinkCommand(linkManager, config));

        getProxy().getScheduler().schedule(this, () -> linkManager.save(), 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        linkManager.save();
    }

    private void saveResource(String name, boolean replace) {
        try {
            getDataFolder().mkdirs();
            File out = new File(getDataFolder(), name);
            if (!out.exists() || replace) {
                out.createNewFile();
                InputStream is = getClass().getResourceAsStream("/" + name);
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
}
