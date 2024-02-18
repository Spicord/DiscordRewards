package me.tini.discordrewards.linking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spicord.bot.DiscordBot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tini.discordrewards.config.Discord;
import me.tini.discordrewards.util.CodeGenerator;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class LinkManager {

    private final Gson gson;
    private final Map<UUID, String> pending;
    private final Map<Long, LinkedAccount> accounts;
    private final File linkedFile;
    private final Map<UUID, String> uuidNameCache;
    private DiscordBot bot;
    private GuildMessageChannel channel;
    private Discord discord;

    public LinkManager(File linkedFile) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pending = new HashMap<UUID, String>();
        this.accounts = new HashMap<Long, LinkedAccount>();
        this.uuidNameCache = new HashMap<UUID, String>();

        this.linkedFile = linkedFile;

        try {
            if (linkedFile.exists()) {
                LinkedAccount[] linked = gson.fromJson(new FileReader(linkedFile), LinkedAccount[].class);

                if (linked == null || linked.length == 0) {
                    return;
                }

                for (LinkedAccount account : linked) {
                    accounts.put(account.getId(), account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public String generateCode() {
        String code;
        do {
            code = CodeGenerator.generateCode(8);
        } while (pending.values().contains(code));
        return code;
    }

    public LinkedAccount getAccount(Long id) {
        return accounts.get(id);
    }

    public LinkedAccount getAccount(UUID uuid) {
        return accounts.values().stream()
                .filter(account -> account.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isVerified(UUID uuid) {
        return accounts.values().stream()
                .map(LinkedAccount::getUniqueId)
                .anyMatch(uuid::equals);
    }

    public void addPendingPlayer(UUID uuid, String name, String code) {
        pending.put(uuid, code);
        uuidNameCache.put(uuid, name);
    }

    public void save() {
        if (accounts.values().size() == 0) {
            return;
        }
        try (OutputStream os = new FileOutputStream(linkedFile)) {
            String json = gson.toJson(accounts.values());
            os.write(json.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidCode(String code) {
        return pending.values().contains(code);
    }

    public LinkedAccount link(Long discordId, String code) {
        for (Entry<UUID, String> entry : pending.entrySet()) {
            UUID uuid = entry.getKey();
            String validCode = entry.getValue();

            if (validCode.equals(code)) {
                LinkedAccount acc = new LinkedAccount(discordId, getName(uuid), uuid.toString(), 0);
                accounts.put(discordId, acc);

                // cleanup
                pending.values().remove(code);
                uuidNameCache.remove(uuid);

                save();

                return acc;
            }
        }
        return null;
    }

    private String getName(UUID uuid) {
        return uuidNameCache.get(uuid);
    }

    public Map<UUID, String> getPending() {
        return pending;
    }

    public Map<Long, LinkedAccount> getAccounts() {
        return accounts;
    }

    public Map<UUID, String> getUuidNameCache() {
        return uuidNameCache;
    }

    public void setBot(DiscordBot bot) {
        this.bot = bot;
    }

    public DiscordBot getBot() {
        return bot;
    }

    public void setChannel(GuildMessageChannel channel) {
        this.channel = channel;
    }

    public GuildMessageChannel getChannel() {
        return channel;
    }

    public void setDiscord(Discord discord) {
        this.discord = discord;
    }

    public Discord getDiscord() {
        return discord;
    }
}
