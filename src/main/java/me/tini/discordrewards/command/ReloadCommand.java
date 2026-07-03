package me.tini.discordrewards.command;

import eu.mcdb.universal.command.UniversalCommandSender;
import eu.mcdb.universal.command.api.Command;
import me.tini.discordrewards.DiscordRewards;

public class ReloadCommand extends Command {

    private DiscordRewards addon;

    public ReloadCommand(DiscordRewards addon) {
        super("discordrewards-reload", "discordrewards.admin.reload");
        this.addon = addon;

        setCommandHandler(this::handle);
    }

    public boolean handle(UniversalCommandSender sender) {
        addon.reload();
        sender.sendMessage("[DiscordRewards] Reloaded the config");
        return true;
    }
}
