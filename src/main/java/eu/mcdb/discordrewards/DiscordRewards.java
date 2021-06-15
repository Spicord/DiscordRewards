package eu.mcdb.discordrewards;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import eu.mcdb.discordrewards.config.Config;
import eu.mcdb.discordrewards.config.Discord;
import eu.mcdb.discordrewards.config.Rewards;
import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;
import org.spicord.bot.command.DiscordBotCommand;
import org.spicord.embed.Embed;
import org.spicord.embed.EmbedLoader;
import eu.mcdb.universal.Server;
import eu.mcdb.universal.player.UniversalPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class DiscordRewards extends SimpleAddon {

    private Logger logger;
    private LinkManager linkManager;
    private Config config;
    private EmbedLoader embedLoader;

    private TextChannel channel;
    private Long channelId;
    private String prefix;

    public DiscordRewards(LinkManager linkManager, Config config) {
        super("DiscordRewards", "rewards", "Sheidy", new String[] { "instructions" });
        this.logger = config.getLogger();
        this.linkManager = linkManager;
        this.config = config;
        this.embedLoader = new EmbedLoader();
    }

    @Override
    public void onReady(DiscordBot bot) {
        this.channelId = config.getDiscord().getChannelId();
        this.channel = bot.getJda().getTextChannelById(channelId);
        this.prefix = bot.getCommandPrefix();

        bot.getJda().addEventListener(new DiscordJoinListener(linkManager, this));

        if (channel == null) {
            logger.warning("===================================================");
            logger.warning("Channel with id '" + channelId + "' wasn't found.");
            logger.warning("The linking system was disabled.");
            logger.warning("===================================================");
        }
    }

    @Override
    public void onShutdown(DiscordBot bot) {
        this.channelId = null;
        this.channel   = null;
        this.prefix    = null;
    }

    @Override
    public void onDisable() {
        this.logger      = null;
        this.linkManager = null;
        this.config      = null;
        this.embedLoader = null;
    }

    @Override
    public void onCommand(DiscordBotCommand command, String[] args) {
        if (command.getMessage().isFromType(ChannelType.TEXT)) {
            if (command.getSender().hasPermission(Permission.MANAGE_CHANNEL)) {
                command.getMessage().delete().queue();
                command.reply(embedLoader.getEmbedByName("instructions"));
            }
        }
    }

    @Override
    public void onMessageReceived(DiscordBot bot, MessageReceivedEvent event) {
        User author = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        String messageStr = message.getContentRaw();

        if (!event.isFromType(ChannelType.TEXT))
            return;

        if (messageStr.equals(prefix + "instructions"))
            return;

        long id = author.getIdLong();
        Account acc = linkManager.getAccount(id);

        if (event.getChannel().getIdLong() == channelId) {

            if (author.isBot()) {
                if (bot.getJda().getSelfUser().getIdLong() != id) {
                    message.delete().queue();
                }
                return;
            }

            String code = messageStr;

            message.delete().queue();

            if (acc != null) return; // already verified

            if (linkManager.isValidCode(code)) {
                Discord dc = config.getDiscord();
                Account account = linkManager.link(author.getIdLong(), code);

                Function<String, String> placeholders = str -> str.replace("{player_name}", account.getName());
                Server server = Server.getInstance();

                if (config.isBroadcastEnabled()) {
                    config.getBroadcastMessage().stream()
                            .map(placeholders)
                            .forEach(server::broadcast);
                }
                if (config.isRewardEnabled()) {
                    config.getRewardCommands().stream()
                            .map(placeholders)
                            .forEach(Config::executeSyncCommand);
                }
                if (dc.shouldSendMessage()) {
                    Embed embed = embedLoader.getEmbedByName("completed");
                    String str = embed.toString()
                            .replace("%player%", account.getName());

                    Embed.fromJson(str).sendToChannel(channel)
                            .delete().queueAfter(10, TimeUnit.SECONDS);
                }
                renameUser(member, account.getName());
                addRole(member);
            } else {
                Embed embed = embedLoader.getEmbedByName("invalid-code");
                embed.sendToChannel(channel).delete().queueAfter(10, TimeUnit.SECONDS);
            }
        } else if (config.isRewardEnabled()) {
            if (acc != null) {
                int count = acc.getMessageCount() + 1;
                acc.setMessageCount(count);
                Rewards rw = config.getRewards();

                if (rw.appliesForReward(count)) {
                    UniversalPlayer p = Server.getInstance().getPlayer(acc.getUniqueId());
                    Rewards.Reward reward = rw.getReward(count);

                    if (p != null) {
                        //rw.getManager().give(reward, p);
                    } else {
                        //rw.getManager().cache(acc, count);
                    }

                    if (rw.shouldSendDiscordMessage()) {
                        Embed embed = embedLoader.getEmbedByName(p != null ? "reached" : "reached-offline");

                        embed = Embed.fromJson(embed.toString()
                                .replace("{user_mention}", author.getAsMention())
                                .replace("{amount}", String.valueOf(count)));

                        embed.sendToChannel(event.getTextChannel());
                    }
                }
            }
        }
    }

    protected void renameUser(Member member, String newName) {
        Discord dc = config.getDiscord();
		Guild guild = member.getGuild();
        if (dc.shouldRenameUser()) {
            Function<String, String> placeholders = str -> str.replace("{player_name}", newName);
            String name = placeholders.apply(dc.getNameTemplate());
            try {
                guild.modifyNickname(member, name).queue();
            } catch (InsufficientPermissionException e) {
                logger.severe("Could't change nickname of member because the bot doesn't have permission to!");
            } catch (HierarchyException e) {
                logger.severe("The bot can't modify the nickname of members with higher hierarchy!");
            }
        }
	}

	protected void addRole(Member member) {
        Discord dc = config.getDiscord();
		Guild guild = member.getGuild();
        if (dc.shouldAddRole()) {
            Role role = dc.getVerifiedRole(guild);
            if (role == null) {
                logger.severe("Could't add role to user '"+member.getAsMention()+"' (role not found!)");
            } else {
                try {
                    guild.addRoleToMember(member, role).queue();
                } catch (InsufficientPermissionException e) {
                    logger.severe("Could't add role to member because the bot doesn't have permission to!");
                } catch (HierarchyException e) {
                    logger.severe("The bot can't modify roles of members with higher hierarchy!");
                }
            }
        }
	}
}
