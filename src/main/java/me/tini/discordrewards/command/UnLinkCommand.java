package me.tini.discordrewards.command;

import java.util.Collection;
import java.util.Iterator;

import eu.mcdb.universal.command.UniversalCommandSender;
import eu.mcdb.universal.command.api.Command;
import eu.mcdb.universal.command.api.CommandParameter;
import eu.mcdb.universal.command.api.CommandParameters;
import me.tini.discordrewards.linking.LinkedAccount;
import me.tini.discordrewards.linking.LinkManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class UnLinkCommand extends Command {

    private final LinkManager linkManager;

    public UnLinkCommand(LinkManager linkManager) {
        super("unlink", "discordrewards.admin.unlink", new String[] { "unlink-discord" });

        this.linkManager = linkManager;

        setParameter(0, new CommandParameter("name", false));
        setCommandHandler(this::handle);
    }

    public boolean handle(UniversalCommandSender sender, CommandParameters parameters) {
        String mcName = parameters.getValue("name");

        Collection<LinkedAccount> accounts = linkManager.getAccounts().values();
        Iterator<LinkedAccount> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            LinkedAccount account = iterator.next();
            if (account.getName().equals(mcName)) {
                iterator.remove();
                linkManager.save();

                if (linkManager.getChannel() != null) {
                    final Guild guild = linkManager.getChannel().getGuild();
                    final Member member = guild.getMemberById(account.getId());
                    final Role role = linkManager.getDiscord().getVerifiedRole(guild);

                    if (member != null && role != null) {
                        guild.removeRoleFromMember(member, role).queue();
                    }
                }

                sender.sendMessage("Unlinked account of player " + mcName);
                return true;
            }
        }

        sender.sendMessage("The player you specified doesn't has a linked account");

        return true;
    }
}
