package eu.mcdb.discordrewards.config;

import java.awt.Color;
import java.util.List;
import java.util.logging.Logger;
import eu.mcdb.universal.config.YamlConfiguration;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class Discord {

    private boolean addRole;
    private boolean renameUser;
    @Getter private Long channelId;
    private boolean sendMessage;
    @Getter private String nameTemplate;
    private String roleType;
    private String role;
    private Logger logger;

    Discord(Logger logger, YamlConfiguration config) {
        this.logger       = logger;
        this.addRole      = config.getBoolean("add-role.enabled", false);
        this.roleType     = config.getString("add-role.type");
        this.role         = config.getString("add-role.role");
        this.channelId    = config.getLong("channel-id");
        this.sendMessage  = config.getBoolean("send-message", false);
        this.renameUser   = config.getBoolean("rename-user", false);
        this.nameTemplate = config.getString("new-name");

        if (!(roleType.equals("name") || roleType.equals("id"))) {
            roleType = "name";
            role = "Verified";
            logger.warning("'add-role.type' should be 'name' or 'id', you have put '" + roleType + "'!");
            logger.warning("Using 'name=Verified' as default!");
        }
    }

    public boolean shouldAddRole() {
        return addRole;
    }

    public boolean shouldRenameUser() {
        return renameUser;
    }

    public boolean shouldSendMessage() {
        return sendMessage;
    }

    public Role getVerifiedRole(Guild guild) {
        if (roleType.equals("name")) {
            List<Role> roles = guild.getRolesByName(role, false);
            return roles.size() > 0 ? roles.get(0) : createRole(guild);
        } else {
            return guild.getRoleById(role);
        }
    }

    private Role createRole(Guild guild) {
        if (guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            try {
                logger.warning("The role '" + role + "' was not found, creating it...");
                Role r = guild.createRole()
                              .setName(role)
                              .setColor(Color.GREEN)
                              .setMentionable(false)
                              .complete();
                logger.info("Created role '" + r.getName() + " (" + r.getId() + ")'");
                return r;
            } catch (Exception e) {
                logger.severe("Cannot create the role: " + e.getMessage());
            }
        } else {
            logger.warning("The role '" + role + "' was not found and the bot doesn't has permission to create it.");
        }
        return null;
    }
}
