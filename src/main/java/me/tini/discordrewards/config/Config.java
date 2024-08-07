package me.tini.discordrewards.config;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import eu.mcdb.universal.Server;
import eu.mcdb.universal.config.YamlConfiguration;
import eu.mcdb.util.chat.ChatColor;
import lombok.Getter;
import me.tini.discordrewards.DiscordRewards;

public class Config {

    @Getter private String prefix;
    @Getter private List<String> verifyInstructions;
    @Getter private boolean broadcastEnabled;
    @Getter private List<String> broadcastMessage;
    @Getter private boolean rewardEnabled;
    @Getter private List<String> rewardCommands;
    @Getter private String clickToCopyText;
    @Getter private String alreadyVerifiedMessage;
    @Getter private DiscordConfig discordConfig;
    @Getter private RewardManager rewards;

    @Getter private final File dataFolder;
    @Getter private Logger logger;

    public Config(DiscordRewards addon) {
        this.dataFolder = addon.getDataFolder();
        this.logger = addon.getLogger();

        YamlConfiguration config = YamlConfiguration.load(new File(dataFolder, "config.yml"));
        YamlConfiguration discord = YamlConfiguration.load(new File(dataFolder, "discord.yml"));
        YamlConfiguration rewards = YamlConfiguration.load(new File(dataFolder, "rewards.yml"));

        this.prefix = config.getString("prefix");
        this.verifyInstructions = config.getStringList("verify-instructions");
        this.broadcastEnabled = config.getBoolean("broadcast.enabled");
        this.broadcastMessage = config.getStringList("broadcast.message");

        Function<String, String> filter = str -> {
            str = str.replace("{prefix}", prefix);
            str = ChatColor.translateAlternateColorCodes('&', str);
            return str;
        };

        this.broadcastMessage = broadcastMessage.stream().map(filter).collect(Collectors.toList());
        this.rewardEnabled = config.getBoolean("reward.enabled");
        this.rewardCommands = config.getStringList("reward.commands");
        this.clickToCopyText = config.getString("click-to-copy");
        if (clickToCopyText == null) { clickToCopyText = "Click to copy"; }
        this.alreadyVerifiedMessage = config.getString("already-verified-message");
        this.alreadyVerifiedMessage = alreadyVerifiedMessage.replace("{prefix}", prefix);
        this.alreadyVerifiedMessage = ChatColor.translateAlternateColorCodes('&', alreadyVerifiedMessage);

        this.discordConfig = new DiscordConfig(logger, discord);
        this.rewards = new RewardManager(dataFolder, rewards);
    }

    public List<String> getVerifyInstructions(String code) {
        Function<String, String> filter = str -> {
            str = str.replace("{prefix}", prefix);
            str = str.replace("{code}", code);
            str = ChatColor.translateAlternateColorCodes('&', str);
            return str;
        };

        return verifyInstructions.stream().map(filter).collect(Collectors.toList());
    }

    public static void executeSyncCommand(String command) {
        Server.getInstance().dispatchCommand(command);
    }
}
