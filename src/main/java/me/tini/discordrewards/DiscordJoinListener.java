package me.tini.discordrewards;

import me.tini.discordrewards.linking.LinkedAccount;
import me.tini.discordrewards.linking.LinkManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordJoinListener extends ListenerAdapter {

    private LinkManager linkManager;
    private DiscordRewards addon;

    public DiscordJoinListener(LinkManager linkManager, DiscordRewards addon) {
        this.linkManager = linkManager;
        this.addon = addon;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        LinkedAccount account = linkManager.getAccount(member.getUser().getIdLong());

        if (account != null) {
            addon.getLogger().info("An user previously verified has joined, adding role and renaming if necessary.");
            addon.renameUser(member, account.getPlayerName());
            addon.addRole(member);
        }
    }
}
