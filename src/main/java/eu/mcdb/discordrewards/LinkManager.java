package eu.mcdb.discordrewards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.mcdb.discordrewards.util.RandomUtils;
import eu.mcdb.universal.player.UniversalPlayer;

public class LinkManager {

    private final Gson gson;
    private final Map<UUID, String> pending;
    private final Map<Long, Account> accounts;
    private final File linkedFile;
    private final Map<UUID, String> uuidNameCache;

    public LinkManager(File linkedFile) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pending = new HashMap<UUID, String>();
        this.accounts = new HashMap<Long, Account>();
        this.uuidNameCache = new HashMap<UUID, String>();

        this.linkedFile = linkedFile;

        try {
            if (linkedFile.exists()) {
                Account[] linked = gson.fromJson(new FileReader(linkedFile), Account[].class);

                if (linked == null || linked.length == 0) {
                    return;
                }

                for (Account account : linked) {
                    accounts.put(account.getId(), account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPending(UniversalPlayer player) {
        return pending.containsKey(player.getUniqueId());
    }

    public String generateCode() {
        String code;
        do {
            code = RandomUtils.randomString(8);
        } while (pending.values().contains(code));
        return code;
    }

    public Account getAccount(Long id) {
        return accounts.get(id);
    }

    public Account getAccount(UUID uuid) {
        return accounts.values().stream()
                .filter(account -> account.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isVerified(UniversalPlayer player) {
        UUID uuid = player.getUniqueId();

        return accounts.values().stream()
                .map(Account::getUniqueId)
                .anyMatch(uuid::equals);
    }

    public void addPendingPlayer(UniversalPlayer player, String code) {
        pending.put(player.getUniqueId(), code);
        uuidNameCache.put(player.getUniqueId(), player.getName());
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

    public Account link(Long discordId, String code) {
        for (Entry<UUID, String> entry : pending.entrySet()) {
            UUID uuid = entry.getKey();
            String validCode = entry.getValue();

            if (validCode.equals(code)) {
                Account acc = new Account(discordId, getName(uuid), uuid.toString(), 0);
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

    public Map<Long, Account> getAccounts() {
        return accounts;
    }

    public Map<UUID, String> getUuidNameCache() {
        return uuidNameCache;
    }
}
