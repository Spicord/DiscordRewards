package me.tini.discordrewards.command;

import eu.mcdb.universal.command.UniversalCommandSender;
import eu.mcdb.universal.command.api.Command;
import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.config.Config;
import me.tini.discordrewards.linking.LinkManager;

public class LinkCommand extends Command {

    private final LinkManager linkManager;
    private final Config config;

    public LinkCommand(LinkManager linkManager, Config config) {
        super("link", null, new String[] { "discord" });

        this.linkManager = linkManager;
        this.config = config;

        setCommandHandler(this::handle);
    }

    public boolean handle(UniversalCommandSender sender) {
        if (sender.isPlayer()) {
            UniversalPlayer player = sender.getPlayer();

            if (linkManager.isVerified(player.getUniqueId())) {
                player.sendMessage(config.getAlreadyVerifiedMessage());
            } else {
                String code = linkManager.generateCode();

                if (player.isBukkitPlayer()) {
                    BetterMessage.sendBukkit(player, config, (code));
                } else if (player.isProxiedPlayer()) {
                    BetterMessage.sendBungee(player, config, (code));
                } else {
                    for (String line : config.getVerifyInstructions(code)) {
                        player.sendMessage(line);
                    }
                }

                linkManager.addPendingPlayer(player.getUniqueId(), player.getName(), code);
            }
        } else {
            sender.sendMessage("&cYou need to be a player to run this command!");
        }

        return true;
    }
}
