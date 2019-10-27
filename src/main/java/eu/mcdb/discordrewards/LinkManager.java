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
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.mcdb.discordrewards.util.RandomUtils;

public class LinkManager {

    private final Gson gson;
    private final HashMap<UUID, String> pending;
    private final Map<Long, Account> accounts;
    private final File linkedFile;

    public LinkManager(File linkedFile) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pending = new HashMap<UUID, String>();
        this.accounts = new HashMap<Long, Account>();
        this.linkedFile = linkedFile;

        try {
            if (linkedFile.exists()) {
                Account[] linked = gson.fromJson(new FileReader(linkedFile), Account[].class);

                if (linked == null || linked.length == 0)
                    return;

                for (Account account : linked) {
                    accounts.put(account.getId(), account);
                }
            } else {
                linkedFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPending(Player p) {
        return pending.containsKey(p.getUniqueId());
    }

    public String generateCode(Player player) {
        String seed = player.getUniqueId().toString().replace("-", "").toUpperCase();
        return RandomUtils.randomString(8, seed);
    }

    public Account getAccountByDiscordId(Long id) {
        return accounts.get(id);
    }

    public Account getAccountByUniqueId(UUID uuid) {
        Predicate<Account> filter = a -> a.getUniqueId().equals(uuid);
        return accounts.values().stream().filter(filter).findFirst().orElse(null);
    }

    public boolean isVerified(Player player) {
        UUID uuid = player.getUniqueId();

        return accounts.values().stream()
                .map(Account::getUniqueId)
                .anyMatch(uuid::equals);
    }

    public void addPendingPlayer(Player player, String code) {
        pending.put(player.getUniqueId(), code);
    }

    public void save() {
        if (accounts.values().size() == 0) return;
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
            String _code = entry.getValue();

            if (_code.equals(code)) {
                Account acc = new Account(discordId, getName(uuid), uuid.toString(), 0);
                accounts.put(discordId, acc);
                save();
                return acc;
            }
        }
        return null;
    }

    private String getName(UUID uuid) {
        OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(uuid);
        return p == null ? "" : p.getName();
    }

    // this is temporary
    public void removeCode(String code) {
        pending.values().remove(code);
    }
}
