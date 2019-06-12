package me.ohvalsgod.bridge.command.commands;

import io.github.thatkawaiisam.redstone.shared.RedstoneSharedAPI;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import me.ohvalsgod.bridge.permissions.grant.GrantBuilder;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bukkitlib.command.Command;
import me.ohvalsgod.bukkitlib.command.param.Parameter;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand {

    private static PermissionsHandler permissionsHandler;

    public GrantCommand(BridgePlugin plugin) {
        permissionsHandler = plugin.getPermissionsHandler();
    }

    @Command(
            names = "ogrant",
            permissionNode = "bridge.permissions.grant"
    )
    public static void commandGrant(CommandSender issuer,
                                    @Parameter(name = "player") OfflinePlayer player,
                                    @Parameter(name = "group")PermissionsGroup group,
                                    @Parameter(name = "duration") String duration,
                                    @Parameter(name = "scope") String scope,
                                    @Parameter(name = "reason", wildcard = true) String reason) {

        if (TimeUtils.parseTime(scope) <= 0) {
            issuer.sendMessage(ChatColor.RED + "Duration '" + duration + "' is invalid.");
            return;
        }

        if (RedstoneSharedAPI.serverExists(scope) && !scope.equalsIgnoreCase("all")) {
            issuer.sendMessage(ChatColor.RED + "Server '" + scope + "' does not exist.");
            return;
        }

        PermissionsUser user = (permissionsHandler.userExists(player.getUniqueId()) ? permissionsHandler.getUser(player.getUniqueId()):permissionsHandler.createUser(player.getUniqueId()));

        user.getGrants().add(new GrantBuilder((issuer instanceof Player) ? ((Player)issuer).getUniqueId():null)
                .permissionsGroup(group.getUUID())
                .duration(TimeUtils.parseTime(duration))
                .scope(scope)
                .reason(reason)
                .get());
        user.update();

        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Successfully granted '&r" + group.getFormattedName(player.getName()) + "&6' the " + group.getFancyName() + "&6 group."));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Duration: &r" + WordUtils.capitalize(duration)));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Scope: &r" + RedstoneSharedAPI.getServer(scope).getServerID()));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reason: &r") + reason);

        if (player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            onlinePlayer.sendMessage("&7You now have access to the &r" + group.getFancyName() + "&7group.");
        }
    }

}
