package eu.mcdb.discordrewards.config;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.universal.config.YamlConfiguration;

public class Rewards {

    private Map<String, Reward> rewards;
    private boolean sendDiscordMessage;
    private Map<UUID, Set<Integer>> cached;
    private File cachedFile;
    private Gson gson;

    Rewards(File dataFolder, YamlConfiguration config) {
        this.rewards = new HashMap<String, Reward>();
        this.sendDiscordMessage = config.getBoolean("send-discord-message");

        List<?> l = config.getList("message-rewards");
        this.gson = new Gson();

        for (Object i : l) {
            String json = gson.toJson(i);

            Map<String, Reward> obj = gson.fromJson(json, rewardsFileType());

            Entry<String, Reward> commands = obj.entrySet().iterator().next();

            rewards.put(commands.getKey(), commands.getValue());
        }

        this.cachedFile = new File(dataFolder, "cached-rewards.json");
        loadCached();
    }

    private void loadCached() {
        try {
            if (cachedFile.exists()) {
                String json = new String(Files.readAllBytes(cachedFile.toPath()));
                this.cached = gson.fromJson(json, cacheFileType());

                if (this.cached == null) {
                    // should not happen, maybe show a warning.
                }
            }

            if (this.cached == null) {
                this.cached = new HashMap<UUID, Set<Integer>>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCached() {
        try (FileOutputStream fos = new FileOutputStream(cachedFile)) {
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

    public void cache(Account acc, int msgcount) {
        cached.computeIfAbsent(acc.getUniqueId(), u -> new HashSet<Integer>()).add(msgcount);
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

        public String[] getCommands() {
            return commands;
        }
    }

    private Type rewardsFileType() {
        return new TypeToken<Map<String, Reward>>() {}.getType();
    }

    private Type cacheFileType() {
        return new TypeToken<Map<UUID, Set<Integer>>>(){}.getType();
    }
}
