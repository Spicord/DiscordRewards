package me.tini.discordrewards.bukkit;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.spicord.bot.DiscordBot;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tini.discordrewards.linking.LinkedAccount;
import net.dv8tion.jda.api.entities.User;

public class PlaceholdersImpl extends PlaceholderExpansion {

    private final DiscordRewardsBukkit plugin;

    public PlaceholdersImpl(DiscordRewardsBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getIdentifier() {
        return "discordrewards";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return onRequest(player, params);
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return null;
        }

        if ("verifycode".equals(params)) {
            String code = plugin.getAddon().getLinkManager().getPending().get(player.getUniqueId());
            if (code == null) {
                return "None";
            }
            return code;
        }

        if ("discorduser".equals(params)) {
            LinkedAccount account = plugin.getAddon().getLinkManager().getAccount(player.getUniqueId());
            if (account != null) {
                long discordId = account.getDiscordId();

                DiscordBot bot = plugin.getAddon().getLinkManager().getBot();

                if (bot != null && bot.isConnected()) {
                    User user = bot.getJda().getUserById(discordId);

                    if (user != null) {
                        return user.getName();
                    }
                }
                return String.valueOf(discordId);
            }
            return "None";
        }
        return null;
    }

    public void registerNow() {
        PlaceholderAPI.registerExpansion(this);
    }
}
