package eu.mcdb.discordrewards.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.discordrewards.config.Config;

public class LinkCommand implements CommandExecutor {

    private final LinkManager linkManager;
    private final Config config;

    public LinkCommand(LinkManager linkManager, Config config) {
        this.linkManager = linkManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (linkManager.isVerified(player)) {
                player.sendMessage(config.getAlreadyVerifiedMessage());
            } else {
                String code = linkManager.generateCode(player);

                for (String s : config.getVerifyInstructions(code)) {
                    player.sendMessage(s);
                }

                linkManager.addPendingPlayer(player, code);
            }
        } else {
            sender.sendMessage("You need to be a player to run this command!");
        }
        return true;
    }
}
