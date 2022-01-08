package eu.mcdb.discordrewards.command;

import java.util.Collection;
import java.util.Iterator;

import eu.mcdb.discordrewards.Account;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.universal.command.UniversalCommandSender;
import eu.mcdb.universal.command.api.Command;
import eu.mcdb.universal.command.api.CommandParameter;
import eu.mcdb.universal.command.api.CommandParameters;

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

        Collection<Account> accounts = linkManager.getAccounts().values();
        Iterator<Account> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            if (account.getName().equals(mcName)) {
                iterator.remove();
                linkManager.save();
                sender.sendMessage("Unlinked account of player " + mcName);
                return true;
            }
        }

        sender.sendMessage("The player you specified doesn't has a linked account");

        return true;
    }
}
