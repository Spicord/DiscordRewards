package me.tini.discordrewards.command;

import eu.mcdb.universal.player.UniversalPlayer;
import eu.mcdb.util.chat.ChatColor;
import me.tini.discordrewards.config.Config;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BetterMessage {

    private static final String CODE_PLACEHOLDER = "{code}";

    public static void sendBukkit(UniversalPlayer player, Config config, String code) {
        for (String line : config.getVerifyInstructions()) {
            TextComponent tc = createTextComponent(line, config, code);

            player.getBukkitPlayer().spigot().sendMessage(tc);
        }
    }

    public static void sendBungee(UniversalPlayer player, Config config, String code) {
        for (String line : config.getVerifyInstructions()) {
            TextComponent tc = createTextComponent(line, config, code);

            player.getProxiedPlayer().sendMessage(tc);
        }
    }

    private static TextComponent createTextComponent(String line, Config config, String code) {
        line = line.replace("{prefix}", config.getPrefix());
        line = ChatColor.translateAlternateColorCodes('&', line);

        TextComponent tc;

        int index = line.indexOf(CODE_PLACEHOLDER);
        if (index != -1) {
            String before = line.substring(0, index);
            String after = line.substring(index + CODE_PLACEHOLDER.length());
            tc = new TextComponent(before);

            TextComponent clickableCode = new TextComponent(code);
            clickableCode.setClickEvent(new ClickEvent(getClickEventAction(), code));
            clickableCode.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(config.getClickToCopyText()).create()));

            tc.addExtra(clickableCode);

            tc.addExtra(after);
        } else {
            tc = new TextComponent(line);
        }
        return tc;
    }

    private static ClickEvent.Action getClickEventAction() {
        ClickEvent.Action action = null;
        try {
            action = ClickEvent.Action.valueOf("COPY_TO_CLIPBOARD");
        } catch (IllegalArgumentException e) {}
        if (action == null) {
            action = ClickEvent.Action.SUGGEST_COMMAND;
        }
        return action;
    }
}
