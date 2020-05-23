package eu.mcdb.discordrewards.api;

import org.spicord.api.services.linking.LinkData;
import org.spicord.api.services.linking.LinkingService;
import org.spicord.api.services.linking.PendingLinkData;
import eu.mcdb.discordrewards.Account;
import eu.mcdb.discordrewards.LinkManager;
import eu.mcdb.universal.player.UniversalPlayer;

public class LinkingServiceImpl implements LinkingService {

    private final LinkManager lm;

    public LinkingServiceImpl(LinkManager lm) {
        this.lm = lm;
    }

    @Override
    public String id() {
        return "drewards-link";
    }

    @Override
    public boolean isPending(UniversalPlayer player) {
        return lm.isPending(player);
    }

    @Override
    public boolean isLinked(UniversalPlayer player) {
        return lm.getAccount(player.getUniqueId()) != null;
    }

    @Override
    public LinkData link(PendingLinkData data, long id) {
        Account acc = new Account(id, data.getName(), data.getUniqueId().toString(), 0);
        lm.getAccounts().put(id, acc);
        lm.save();
        lm.getPending().remove(data.getUniqueId());
        return new LinkData(id, data.getName(), data.getUniqueId().toString());
    }

    @Override
    public boolean unlink(LinkData data) {
        if (lm.getAccounts().containsKey(data.getId())) {
            lm.getAccounts().remove(data.getId());
            lm.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean addPendingLink(PendingLinkData data) {
        lm.getPending().put(data.getUniqueId(), data.getName());
        return true;
    }
}
