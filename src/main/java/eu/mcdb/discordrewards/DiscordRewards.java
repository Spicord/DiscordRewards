package eu.mcdb.discordrewards;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import eu.mcdb.discordrewards.config.Config;
import eu.mcdb.discordrewards.config.Config.Discord;
import eu.mcdb.discordrewards.config.Config.Rewards;
import eu.mcdb.discordrewards.config.Config.Rewards.Reward;
import eu.mcdb.spicord.api.addon.SimpleAddon;
import eu.mcdb.spicord.bot.DiscordBot;
import eu.mcdb.spicord.bot.command.DiscordBotCommand;
import eu.mcdb.spicord.embed.Embed;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.spicord.embed.EmbedSender;
import eu.mcdb.universal.Server;
import eu.mcdb.universal.player.UniversalPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class DiscordRewards extends SimpleAddon {

    private Logger logger;
    private LinkManager lm;
    private Config config;
    private TextChannel channel;
    private Long channelId;
    private EmbedLoader embedLoader;
    private String prefix;

    public DiscordRewards(LinkManager linkManager, Config config) {
        super("DiscordRewards", "rewards", "OopsieWoopsie", new String[] { "instructions" });
        this.logger = config.getLogger();
        this.lm = linkManager;
        this.config = config;
        File embedFolder = new File(config.getDataFolder(), "embed");
        this.embedLoader = new EmbedLoader();
        this.embedLoader.load(embedFolder);
    }

    @Override
    public void onReady(DiscordBot bot) {
        this.channelId = config.getDiscord().getChannelId();
        this.channel = bot.getJda().getTextChannelById(channelId);
        this.prefix = bot.getCommandPrefix();

        bot.getJda().addEventListener(new DiscordJoinListener(lm, this));

        if (channel == null) {
            logger.warning("===================================================");
            logger.warning("Channel with id '" + channelId + "' wasn't found.");
            logger.warning("The linking system was disabled.");
            logger.warning("===================================================");
        }
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
        Account acc = lm.getAccount(id);

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

            if (lm.isValidCode(code)) {
                Discord dc = config.getDiscord();
                Account account = lm.link(author.getIdLong(), code);

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

                    EmbedSender.prepare(channel, Embed.fromJson(str)).complete()
                            .delete().queueAfter(10, TimeUnit.SECONDS);
                }
                renameUser(member, account.getName());
                addRole(member);
            } else {
                Embed embed = embedLoader.getEmbedByName("invalid-code");
                EmbedSender.prepare(channel, embed).complete()
                        .delete().queueAfter(10, TimeUnit.SECONDS);
            }
        } else if (config.isRewardEnabled()) {
            if (acc != null) {
                int count = acc.getMessageCount() + 1;
                acc.setMessageCount(count);
                Rewards rw = config.getRewards();

                if (rw.appliesForReward(count)) {
                    UniversalPlayer p = Server.getInstance().getPlayer(acc.getUniqueId());
                    Reward reward = rw.getReward(count);

                    if (p != null) {
                        reward.give(acc);
                    } else {
                        rw.cache(acc, count);
                    }

                    if (rw.shouldSendDiscordMessage()) {
                        Embed embed = embedLoader.getEmbedByName(p != null ? "reached" : "reached-offline");

                        embed = Embed.fromJson(embed.toString()
                                .replace("{user_mention}", author.getAsMention())
                                .replace("{amount}", String.valueOf(count)));

                        EmbedSender.prepare(event.getTextChannel(), embed).queue();
                    }
                }
            }
        }
    }

    protected void renameUser(Member member, String name1) {
        Discord dc = config.getDiscord();
		Guild guild = member.getGuild();
        if (dc.shouldRenameUser()) {
            Function<String, String> placeholders = str -> str.replace("{player_name}", name1);
            String name = placeholders.apply(dc.getNameTemplate());
            try {
                guild.getController().setNickname(member, name).queue();
            } catch (InsufficientPermissionException e) {
                System.out.println("[ERROR] Could't change nickname of member because the bot doesn't have permission to!");
            } catch (HierarchyException e) {
                System.out.println("[ERROR] The bot can't modify the nickname of members with higher hierarchy!");
            }
        }
	}

	protected void addRole(Member member) {
        Discord dc = config.getDiscord();
		Guild guild = member.getGuild();
        if (dc.shouldAddRole()) {
            Role role = dc.getVerifiedRole(guild);
            if (role == null) {
                System.out.println("[ERROR] Could't add role to user '"+member.getAsMention()+"' (role not found!)");
            } else {
                try {
                    guild.getController().addRolesToMember(member, role).queue();
                } catch (InsufficientPermissionException e) {
                    System.out.println("[ERROR] Could't add role to member because the bot doesn't have permission to!");
                } catch (HierarchyException e) {
                    System.out.println("[ERROR] The bot can't modify roles of members with higher hierarchy!");
                }
            }
        }
	}
}
