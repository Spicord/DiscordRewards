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
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.universal.Server;
import eu.mcdb.universal.config.YamlConfiguration;
import eu.mcdb.universal.player.UniversalPlayer;

public class RewardManager {

    private Map<String, Reward> rewards;
    private boolean shouldSendDiscordMessage;
    private Map<UUID, Set<Integer>> cached;
    private File cachedFile;
    private Gson gson;

    RewardManager(File dataFolder, YamlConfiguration config) {
        this.gson    = new Gson();
        this.rewards = new HashMap<String, Reward>();

        this.shouldSendDiscordMessage = config.getBoolean("send-discord-message");

        List<?> rewardList = config.getList("message-rewards");

        for (Object rawRewardData : rewardList) {
            JsonElement rewardDataJson = gson.toJsonTree(rawRewardData);

            Map<String, Reward> obj = gson.fromJson(rewardDataJson, rewardsFileType());

            Entry<String, Reward> rewardData = obj.entrySet().iterator().next();

            // key -> required message count (string)
            // value -> command list

            rewardData.getValue().requiredMessageCount = Integer.parseInt(rewardData.getKey());

            rewards.put(rewardData.getKey(), rewardData.getValue());
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
        return getCachedData(uuid).stream().map(this::getReward);
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
        return shouldSendDiscordMessage;
    }

    public boolean isCached(UUID uuid) {
        return cached.containsKey(uuid);
    }

    public void cache(Account acc, int msgcount) {
        getCachedData(acc.getUniqueId()).add(msgcount);
        saveCached();
    }

    public void give(Reward reward, UniversalPlayer player) {
        for (String command : reward.commands) {
            Server.getInstance().dispatchCommand(
                command.replace("{player_name}", player.getName())
            );
        }
        getCachedData(player.getUniqueId()).remove(reward.requiredMessageCount);
    }

    public void cleanCache(UUID uuid) {
        cached.remove(uuid);
        saveCached();
    }

    public Reward getReward(int msgcount) {
        return rewards.get(String.valueOf(msgcount));
    }

    public class Reward {

        private transient int requiredMessageCount;

        private String[] commands;

        public String[] getCommands() {
            return commands;
        }
    }

    private Type rewardsFileType() {
        return new TypeToken<Map<String, Reward>>(){}.getType();
    }

    private Type cacheFileType() {
        return new TypeToken<Map<UUID, Set<Integer>>>(){}.getType();
    }

    private Set<Integer> getCachedData(UUID uuid) {
        return cached.computeIfAbsent(uuid, u -> new HashSet<>());
    }
}
