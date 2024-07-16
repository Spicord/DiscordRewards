package me.tini.discordrewards;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.spicord.Spicord;
import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;
import org.spicord.bot.command.SlashCommand;
import org.spicord.embed.Embed;
import org.spicord.embed.EmbedLoader;

import eu.mcdb.universal.Server;
import eu.mcdb.universal.player.UniversalPlayer;
import me.tini.discordrewards.command.LinkCommand;
import me.tini.discordrewards.command.UnLinkCommand;
import me.tini.discordrewards.config.Config;
import me.tini.discordrewards.config.DiscordConfig;
import me.tini.discordrewards.config.RewardManager;
import me.tini.discordrewards.linking.LinkManager;
import me.tini.discordrewards.linking.LinkedAccount;
import me.tini.discordrewards.linking.LinkingServiceImpl;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordRewards extends SimpleAddon {

    private LinkManager linkManager;
    private Config config;
    private EmbedLoader embedLoader;

    private GuildMessageChannel channel;
    private Long channelId;

    private LinkingServiceImpl linkingService;
    private DiscordRewardsPlugin plugin;

    public DiscordRewards(DiscordRewardsPlugin plugin) {
        super(
            "DiscordRewards",
            "rewards",
            "Tini",
            plugin.getVersion()
        );
        this.plugin = plugin;
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void onRegister(Spicord spicord) {
        saveResource("config.yml", false);
        saveResource("discord.yml", false);
        saveResource("rewards.yml", false);

        this.config = new Config(this);
        this.linkManager = new LinkManager(new File(getDataFolder(), "linked.json"));
        this.embedLoader = extractEmbeds();

        linkingService = new LinkingServiceImpl(linkManager, spicord);
        linkingService.register();

        new LinkCommand(linkManager, config).register(plugin);
        new UnLinkCommand(linkManager).register(plugin);
    }

    private EmbedLoader extractEmbeds() {
        try {
            return EmbedLoader.extractAndLoad(getFile(), new File(getDataFolder(), "embed"));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        try {
            getDataFolder().mkdir();
            File out = new File(getDataFolder(), resourcePath);
            if (!out.exists() || replace) {
                InputStream in = getClass().getResourceAsStream("/" + resourcePath);
                Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<GatewayIntent> getRequiredIntents() {
        return Arrays.asList(GatewayIntent.MESSAGE_CONTENT);
    }

    @Override
    public void onReady(DiscordBot bot) {
        this.channelId = config.getDiscordConfig().getChannelId();
        this.channel = bot.getJda().getTextChannelById(channelId);

        if (channel == null) {
            getLogger().warning("===================================================");
            getLogger().warning("Channel with id '" + channelId + "' was not found.");
            getLogger().warning("The linking system is now disabled.");
            getLogger().warning("===================================================");

            return;
        }

        this.linkManager.setBot(bot);
        this.linkManager.setGuild(channel.getGuild());
        this.linkManager.setDiscordConfig(config.getDiscordConfig());

        bot.getJda().addEventListener(new DiscordJoinListener(linkManager, this));

        // === Slash Commands below this line ===

        Guild guild = channel.getGuild();

        SlashCommand instructionsCommand = bot.commandBuilder("instructions", "Send the linking instructions")
            .setDefaultPermissions(Permission.MANAGE_CHANNEL)
            .setExecutor(e -> {
                MessageEmbed embed = embedLoader.getEmbedByName("instructions").toJdaEmbed();

                e.replyEmbeds(embed).queue();
            });

        bot.registerCommand(instructionsCommand, guild);

        SlashCommand linkCommand = bot.commandBuilder("link", "Link your Minecraft account to your Discord account")
            .addOption(OptionType.STRING, "code", "Your linking code", true, false)
            .setExecutor(this::handleLinkCommand);

        bot.registerCommand(linkCommand, guild);
    }

    @Override
    public void onShutdown(DiscordBot bot) {
        this.channelId = null;
        this.channel   = null;
    }

    @Override
    public void onDisable() {
        this.linkManager = null;
        this.config      = null;
        this.embedLoader = null;
    }

    @Override
    public void onMessageReceived(DiscordBot bot, MessageReceivedEvent event) {
        if (config.isRewardEnabled()) {
            final User user = event.getAuthor();
            final long userId = user.getIdLong();
            final LinkedAccount accout = linkManager.getAccount(userId);

            if (accout != null) {
                final int newMessageCount = accout.getMessageCount() + 1;

                accout.setMessageCount(newMessageCount);

                RewardManager manager = config.getRewards();

                if (manager.appliesForReward(newMessageCount)) {
                    UniversalPlayer player = Server.getInstance().getPlayer(accout.getPlayerId());
                    RewardManager.Reward reward = manager.getReward(newMessageCount);

                    if (player != null) {
                        manager.give(reward, player);
                    } else {
                        manager.cache(accout, newMessageCount);
                    }

                    if (manager.shouldSendDiscordMessage()) {
                        Embed embed = embedLoader.getEmbedByName(player != null ? "reached" : "reached-offline");

                        embed = Embed.fromJson(embed.toString()
                                .replace("{user_mention}", user.getAsMention())
                                .replace("{amount}", String.valueOf(newMessageCount)));

                        embed.sendToChannel(event.getGuildChannel());
                    }
                }
            }
        }
    }

    private void handleLinkCommand(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final long memberId = member.getIdLong();

        if (linkManager.isLinked(memberId)) {
            // TODO: already verified embed
            event.reply("Already verified").setEphemeral(true).queue();
            return;
        }

        final String code = event.getOption("code").getAsString();

        if (linkManager.isValidCode(code)) {
            DiscordConfig discordConfig = config.getDiscordConfig();
            LinkedAccount account = linkManager.link(memberId, code);

            Function<String, String> placeholders = str -> str.replace("{player_name}", account.getPlayerName());
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

            Embed embed = embedLoader.getEmbedByName("completed");
            String str = embed.toString().replace("%player%", account.getPlayerName());
            embed = Embed.fromJson(str);

            final boolean isEphemeralMessage = !discordConfig.shouldSendMessage();

            event.replyEmbeds(embed.toJdaEmbed()).setEphemeral(isEphemeralMessage).queue();

            renameUser(member, account.getPlayerName());
            addRole(member);
        } else {
            Embed embed = embedLoader.getEmbedByName("invalid-code");
            event.replyEmbeds(embed.toJdaEmbed()).setEphemeral(true).queue();
        }
    }

    protected void renameUser(Member member, String newNameTemplate) {
        DiscordConfig discordConfig = config.getDiscordConfig();

        if (discordConfig.shouldRenameUser()) {
	        Guild guild = member.getGuild();
            Function<String, String> placeholders = str -> str.replace("{player_name}", newNameTemplate);
            String newName = placeholders.apply(discordConfig.getNameTemplate());

            try {
                guild.modifyNickname(member, newName).queue();
            } catch (InsufficientPermissionException e) {
                getLogger().severe("Could't change nickname of member because the bot doesn't have permission to!");
            } catch (HierarchyException e) {
                getLogger().severe("The bot can't modify the nickname of members with higher hierarchy!");
            }
        }
	}

	protected void addRole(Member member) {
        DiscordConfig discordConfig = config.getDiscordConfig();

        if (discordConfig.shouldAddRole()) {
            Guild guild = member.getGuild();
            Role role = discordConfig.getVerifiedRole(guild);

            if (role == null) {
                getLogger().severe("Could't add role to user '"+member.getAsMention()+"' (role not found!)");
            } else {
                try {
                    guild.addRoleToMember(member, role).queue();
                } catch (InsufficientPermissionException e) {
                    getLogger().severe("Could't add role to member because the bot doesn't have permission to!");
                } catch (HierarchyException e) {
                    getLogger().severe("The bot can't modify roles of members with higher hierarchy!");
                }
            }
        }
	}
}
