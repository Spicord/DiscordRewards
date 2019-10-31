package eu.mcdb.discordrewards.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.discordrewards.BukkitPlugin;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.md_5.bungee.api.ChatColor;

public class Config {

    private static BukkitPlugin plugin;
    private Gson gson;
    @Getter
    private String prefix;
    private List<String> verifyInstructions;
    @Getter
    private boolean broadcastEnabled;
    @Getter
    private List<String> broadcastMessage;
    @Getter
    private boolean rewardEnabled;
    @Getter
    private List<String> rewardCommands;
    @Getter
    private String alreadyVerifiedMessage;
    @Getter
    private Discord discord;
    @Getter
    private Rewards rewards;

    public Config(BukkitPlugin plugin, Map<String, FileConfiguration> cn) {
        Config.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        FileConfiguration config = cn.get("config");
        FileConfiguration discord = cn.get("discord");
        FileConfiguration rewards = cn.get("rewards");

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
        this.alreadyVerifiedMessage = config.getString("already-verified-message");
        this.alreadyVerifiedMessage = alreadyVerifiedMessage.replace("{prefix}", prefix);
        this.alreadyVerifiedMessage = ChatColor.translateAlternateColorCodes('&', alreadyVerifiedMessage);

        this.discord = new Discord(discord);
        this.rewards = new Rewards(rewards);
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

    public class Discord {

        private boolean addRole;
        private boolean renameUser;
        @Getter
        private Long channelId;
        private boolean sendMessage;
        @Getter
        private String nameTemplate;
        private String roleType;
        private String role;

        private Discord(FileConfiguration config) {
            this.addRole = config.getBoolean("add-role.enabled", false);
            this.roleType = config.getString("add-role.type");
            this.role = config.getString("add-role.role");
            this.channelId = config.getLong("channel-id");
            this.sendMessage = config.getBoolean("send-message", false);
            this.renameUser = config.getBoolean("rename-user", false);
            this.nameTemplate = config.getString("new-name");

            if (!(roleType.equals("name") || roleType.equals("id"))) {
                throw new IllegalArgumentException("'add-role.type' should be 'name' or 'id', you have put '" + roleType + "'!");
            }
        }

        public boolean shouldAddRole() {
            return addRole;
        }

        public boolean shouldRenameUser() {
            return renameUser;
        }

        public boolean shouldSendMessage() {
            return sendMessage;
        }

        public Role getRole(Guild guild) {
            if (roleType.equals("name")) {
                List<Role> roles = guild.getRolesByName(role, false);
                return roles.size() > 0 ? roles.get(0) : null;
            } else {
                return guild.getRoleById(role);
            }
        }
    }

    public class Rewards {

        private Map<String, Reward> rewards;
        private boolean sendDiscordMessage;
        private Map<UUID, Set<Integer>> cached;
        private File cachedFile;

        private Rewards(FileConfiguration config) {
            this.rewards = new HashMap<String, Reward>();
            this.sendDiscordMessage = config.getBoolean("send-discord-message");

            // warning: variable naming is too difficult :c

            List<?> l = config.getList("message-rewards");
            Gson gson = new Gson();

            Type type = new TypeToken<Map<String, Reward>>() {}.getType();

            for (Object i : l) {
                String json = gson.toJson(i);

                Map<String, Reward> obj = gson.fromJson(json, type);
                Entry<String, Reward> data = obj.entrySet().iterator().next();
                rewards.put(data.getKey(), data.getValue());
            }

            this.cachedFile = new File(plugin.getDataFolder(), "cached-rewards.json");
            loadCached();
        }

        private void loadCached() {
            try {
                if (cachedFile.exists()) {
                    byte[] b = Files.readAllBytes(cachedFile.toPath());
                    String json = new String(b);
                    Type type = new TypeToken<Map<UUID, Set<Integer>>>() {}.getType();
                    this.cached = gson.fromJson(json, type);
                    if (this.cached == null)
                        this.cached = new HashMap<UUID, Set<Integer>>();
                } else {
                    cachedFile.createNewFile();
                    this.cached = new HashMap<UUID, Set<Integer>>();
                }
                saveCached();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void saveCached() {
            try (OutputStream fos = new FileOutputStream(cachedFile)) {
                String json = gson.toJson(cached);
                fos.write(json.getBytes());
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Stream<Reward> getCachedRewards(UUID uuid) {
            return cached.get(uuid).stream().map(this::getReward);
        }

        public boolean appliesForReward(int msgcount) {
            String msgc = String.valueOf(msgcount);

            for (Entry<String, Reward> r : rewards.entrySet()) {
                if (msgc.equals(r.getKey())) {
                    return true;
                }
            }
            return false;
        }

        public boolean shouldSendDiscordMessage() {
            return sendDiscordMessage;
        }

        public boolean isCached(UUID uuid) {
            return cached.containsKey(uuid);
        }

        public void cache(Account acc, int count) {
            if (cached.containsKey(acc.getUniqueId())) {
                cached.get(acc.getUniqueId()).add(count);
            } else {
                Set<Integer> s = new HashSet<Integer>();
                s.add(count);
                cached.put(acc.getUniqueId(), s);
            }
            saveCached();
        }

        public void cleanCache(UUID uuid) {
            cached.remove(uuid);
            saveCached();
        }

        public Reward getReward(int msgcount) {
            return rewards.get(String.valueOf(msgcount));
        }

        public class Reward {

            private String[] commands;

            public void give(Account acc) {
                Function<String, String> placeholders = c -> c.replace("{player_name}", acc.getName());

                Arrays.asList(commands).stream()
                        .map(placeholders)
                        .forEach(Config::executeSyncCommand);
            }

        }
    }

    public static void executeSyncCommand(String cmd) {
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        });
    }
}
