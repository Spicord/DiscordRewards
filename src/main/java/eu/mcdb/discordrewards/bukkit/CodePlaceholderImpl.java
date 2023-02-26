package eu.mcdb.discordrewards.bukkit;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class CodePlaceholderImpl extends PlaceholderExpansion {

	private DiscordRewardsBukkit plugin;

	public CodePlaceholderImpl(DiscordRewardsBukkit plugin) {
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
	public String onPlaceholderRequest(Player p, String params) {
		if (p == null) {
			return "None";
		}

		if ("verifycode".equals(params)) {
			String code = plugin.getLinkManager().getPending().get(p.getUniqueId());
			if (code == null) {
				return "None";
			}
			return code;
		}

		return "None";
	}

	public void registerNow() {
    	PlaceholderAPI.registerExpansion(this);
	}
}
