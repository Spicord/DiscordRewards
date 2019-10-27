package eu.mcdb.discordrewards;

import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNSFWEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateParentEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateBitrateEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateParentEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.core.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateRolesEvent;
import net.dv8tion.jda.core.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateExplicitContentLevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateFeaturesEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateSystemChannelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageEmbedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateHoistedEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateEmailEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Test extends ListenerAdapter {

    //JDA Events
    public void onReady(ReadyEvent event) {}
    public void onResume(ResumedEvent event) {}
    public void onReconnect(ReconnectedEvent event) {}
    public void onDisconnect(DisconnectEvent event) {}
    public void onShutdown(ShutdownEvent event) {}
    public void onStatusChange(StatusChangeEvent event) {}
    public void onException(ExceptionEvent event) {}

    //User Events
    public void onUserUpdateName(UserUpdateNameEvent event) {}
    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {}
    public void onUserUpdateAvatar(UserUpdateAvatarEvent event) {}
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {}
    public void onUserUpdateGame(UserUpdateGameEvent event) {}
    public void onUserTyping(UserTypingEvent event) {}

    //Self Events. Fires only in relation to the currently logged in account.
    public void onSelfUpdateAvatar(SelfUpdateAvatarEvent event) {}
    public void onSelfUpdateEmail(SelfUpdateEmailEvent event) {}
    public void onSelfUpdateMFA(SelfUpdateMFAEvent event) {}
    public void onSelfUpdateName(SelfUpdateNameEvent event) {}
    public void onSelfUpdateVerified(SelfUpdateVerifiedEvent event) {}

    //Message Events
    //Guild (TextChannel) Message Events
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {}
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {}
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {}
    public void onGuildMessageEmbed(GuildMessageEmbedEvent event) {}
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {}
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {}
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event) {}

    //Private Message Events
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {}
    public void onPrivateMessageUpdate(PrivateMessageUpdateEvent event) {}
    public void onPrivateMessageDelete(PrivateMessageDeleteEvent event) {}
    public void onPrivateMessageEmbed(PrivateMessageEmbedEvent event) {}
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {}
    public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent event) {}

    //Combined Message Events (Combines Guild and Private message into 1 event)
    public void onMessageReceived(MessageReceivedEvent event) {}
    public void onMessageUpdate(MessageUpdateEvent event) {}
    public void onMessageDelete(MessageDeleteEvent event) {}
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {}
    public void onMessageEmbed(MessageEmbedEvent event) {}
    public void onMessageReactionAdd(MessageReactionAddEvent event) {}
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {}
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {}

    //TextChannel Events
    public void onTextChannelDelete(TextChannelDeleteEvent event) {}
    public void onTextChannelUpdateName(TextChannelUpdateNameEvent event) {}
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {}
    public void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent event) {}
    public void onTextChannelUpdatePermissions(TextChannelUpdatePermissionsEvent event) {}
    public void onTextChannelUpdateNSFW(TextChannelUpdateNSFWEvent event) {}
    public void onTextChannelUpdateParent(TextChannelUpdateParentEvent event) {}
    public void onTextChannelUpdateSlowmode(TextChannelUpdateSlowmodeEvent event) {}
    public void onTextChannelCreate(TextChannelCreateEvent event) {}

    //VoiceChannel Events
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {}
    public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {}
    public void onVoiceChannelUpdatePosition(VoiceChannelUpdatePositionEvent event) {}
    public void onVoiceChannelUpdateUserLimit(VoiceChannelUpdateUserLimitEvent event) {}
    public void onVoiceChannelUpdateBitrate(VoiceChannelUpdateBitrateEvent event) {}
    public void onVoiceChannelUpdatePermissions(VoiceChannelUpdatePermissionsEvent event) {}
    public void onVoiceChannelUpdateParent(VoiceChannelUpdateParentEvent event) {}
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {}

    //Category Events
    public void onCategoryDelete(CategoryDeleteEvent event) {}
    public void onCategoryUpdateName(CategoryUpdateNameEvent event) {}
    public void onCategoryUpdatePosition(CategoryUpdatePositionEvent event) {}
    public void onCategoryUpdatePermissions(CategoryUpdatePermissionsEvent event) {}
    public void onCategoryCreate(CategoryCreateEvent event) {}

    //PrivateChannel Events
    public void onPrivateChannelCreate(PrivateChannelCreateEvent event) {}
    public void onPrivateChannelDelete(PrivateChannelDeleteEvent event) {}

    //Guild Events
    public void onGuildReady(GuildReadyEvent event) {}
    public void onGuildJoin(GuildJoinEvent event) {}
    public void onGuildLeave(GuildLeaveEvent event) {}
    public void onGuildAvailable(GuildAvailableEvent event) {}
    public void onGuildUnavailable(GuildUnavailableEvent event) {}
    public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {}
    public void onGuildBan(GuildBanEvent event) {}
    public void onGuildUnban(GuildUnbanEvent event) {}

    //Guild Update Events
    public void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) {}
    public void onGuildUpdateSystemChannel(GuildUpdateSystemChannelEvent event) {}
    public void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) {}
    public void onGuildUpdateExplicitContentLevel(GuildUpdateExplicitContentLevelEvent event) {}
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {}
    public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) {}
    public void onGuildUpdateName(GuildUpdateNameEvent event){}
    public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) {}
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {}
    public void onGuildUpdateRegion(GuildUpdateRegionEvent event) {}
    public void onGuildUpdateSplash(GuildUpdateSplashEvent event) {}
    public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) {}
    public void onGuildUpdateFeatures(GuildUpdateFeaturesEvent event) {}

    //Guild Member Events
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {}
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {}
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {}
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {}
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {}

    //Guild Voice Events
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {}
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {}
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {}
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {}
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {}
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {}
    public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {}
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {}
    public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {}
    public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {}
    public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {}

    //Role events
    public void onRoleCreate(RoleCreateEvent event) {}
    public void onRoleDelete(RoleDeleteEvent event) {}

    //Role Update Events
    public void onRoleUpdateColor(RoleUpdateColorEvent event) {}
    public void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) {}
    public void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) {}
    public void onRoleUpdateName(RoleUpdateNameEvent event) {}
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {}
    public void onRoleUpdatePosition(RoleUpdatePositionEvent event) {}

    //Emote Events
    public void onEmoteAdded(EmoteAddedEvent event) {}
    public void onEmoteRemoved(EmoteRemovedEvent event) {}

    //Emote Update Events
    public void onEmoteUpdateName(EmoteUpdateNameEvent event) {}
    public void onEmoteUpdateRoles(EmoteUpdateRolesEvent event) {}

}
