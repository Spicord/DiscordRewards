package me.tini.discordrewards.linking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.spicord.Spicord;
import org.spicord.api.services.ServiceManager;
import org.spicord.api.services.linking.LinkData;
import org.spicord.api.services.linking.LinkingService;
import org.spicord.api.services.linking.PendingLinkData;

import eu.mcdb.universal.player.UniversalPlayer;

public class LinkingServiceImpl implements LinkingService {

    private final LinkManager lm;
    private final ServiceManager sm;

    public LinkingServiceImpl(LinkManager lm, Spicord sp) {
        this.lm = lm;
        this.sm = sp.getServiceManager();
    }

    public void register() {
        sm.registerService(LinkingService.class, this);
    }

    public void unregister() {
        sm.unregisterService(this);
    }

    @Override
    public String id() {
        return "drewards-link";
    }

    @Override
    public boolean isPending(UniversalPlayer player) {
        return lm.isPending(player.getUniqueId());
    }

    @Override
    public boolean isLinked(UniversalPlayer player) {
        return lm.getAccount(player.getUniqueId()) != null;
    }

    @Override
    public LinkData createLink(PendingLinkData data, long id) {
        LinkedAccount acc = new LinkedAccount(id, data.getPlayerName(), data.getPlayerId().toString(), 0);
        lm.getAccounts().put(id, acc);
        lm.save();
        lm.getPending().remove(data.getPlayerId());
        return data.complete(id);
    }

    @Override
    public boolean removeLink(LinkData data) {
        if (lm.getAccounts().containsKey(data.getDiscordId())) {
            lm.getAccounts().remove(data.getDiscordId());
            lm.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean addPending(PendingLinkData data) {
        lm.getPending().put(data.getPlayerId(), data.getPlayerName());
        return true;
    }

    @Override
    public boolean removePending(PendingLinkData data) {
        return lm.getPending().remove(data.getPlayerId()) != null;
    }

    @Override
    public LinkData[] getLinked() {
        Collection<LinkedAccount> acc = lm.getAccounts().values();
        return acc.toArray(new LinkData[acc.size()]);
    }

    @Override
    public boolean isLinked(UUID id) {
        return lm.getAccount(id) != null;
    }

    @Override
    public boolean isLinked(Long id) {
        return lm.getAccount(id) != null;
    }

    @Override
    public PendingLinkData[] getPending() {
        List<PendingLinkData> list = new ArrayList<PendingLinkData>();
        for (Entry<UUID, String> e : lm.getPending().entrySet()) {
            list.add(new PendingLinkData(e.getValue(), e.getKey()));
        }
        return list.toArray(new PendingLinkData[list.size()]);
    }

    @Override
    public LinkData getLinkData(Long discordId) {
        return lm.getAccount(discordId);
    }

    @Override
    public LinkData getLinkData(UUID playerId) {
        return lm.getAccount(playerId);
    }
}
