package me.ohvalsgod.bridge.command.commands;

import io.github.thatkawaiisam.redstone.shared.RedstoneSharedAPI;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.grant.GrantBuilder;
import me.ohvalsgod.bukkitlib.command.Command;
import me.ohvalsgod.bukkitlib.command.param.Parameter;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand {

    private static PermissionsHandler permissionsHandler = BridgePlugin.getBridgeInstance().getPermissionsHandler();
    private static Database database = BridgePlugin.getBridgeInstance().getMongo();

    @Command(
            names = "ogrant",
            permissionNode = "bridge.permissions.grant"
    )
    public static void commandGrant(CommandSender issuer,
                                    @Parameter(name = "player") PermissionsUser user,
                                    @Parameter(name = "group")PermissionsGroup group,
                                    @Parameter(name = "duration") String duration,
                                    @Parameter(name = "scope") String scope,
                                    @Parameter(name = "reason", wildcard = true) String reason) {
        if (TimeUtils.parseTime(duration) <= 0) {
            issuer.sendMessage(ChatColor.RED + "Duration '" + duration + "' is invalid.");
            return;
        }

        if (RedstoneSharedAPI.serverExists(scope) && !scope.equalsIgnoreCase("all")) {
            issuer.sendMessage(ChatColor.RED + "Server '" + scope + "' does not exist.");
            return;
        }

        user.getGrants().add(new GrantBuilder((issuer instanceof Player) ? ((Player)issuer).getUniqueId():null)
                .permissionsGroup(group)
                .duration(TimeUtils.parseTime(duration))
                .scope(scope)
                .reason(reason)
                .get());

        Player player = user.toPlayer();

        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Successfully granted '&r" + group.getFormattedName(user.getName()) + "&6' the " + group.getFancyName() + "&6 group."));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Duration: &r" + WordUtils.capitalize(duration)));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Scope: &r" + (scope.equalsIgnoreCase("all") ? "ALL":RedstoneSharedAPI.getServer(scope).getServerID())));
        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reason: &r") + reason);

        if (player != null) {
            user.update();
            Player onlinePlayer = player.getPlayer();
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You now have access to the &r" + group.getFancyName() + " &7group."));
        }

        database.getPermissionsUserDAO().saveUser(user);
    }

    @Command(names = "activegrant", permissionNode = "bridge.permissions.grant.active")
    public static void activeGrant(Player player, @Parameter(name = "player") PermissionsUser user) {
        player.sendMessage(ChatColor.YELLOW + "Active grant of '" + user.getActiveGrant().getPermissionsGroup().getFormattedName(user.getName()) + ChatColor.YELLOW + "' is:");
        user.getActiveGrant().show(player);
    }

}
