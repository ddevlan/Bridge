package me.ohvalsgod.bridge.command.commands;

import io.github.thatkawaiisam.redstone.shared.RedstoneSharedAPI;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.grant.GrantBuilder;
import me.ohvalsgod.bridge.permissions.user.grant.menu.GrantDisplayMenu;
import me.ohvalsgod.bridge.permissions.user.grant.process.GrantProcess;
import me.ohvalsgod.bridge.permissions.user.grant.process.menu.GrantProcessMenu;
import me.ohvalsgod.bukkitlib.command.Command;
import me.ohvalsgod.bukkitlib.command.param.Parameter;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommands {

    private static PermissionsHandler permissionsHandler = BridgePlugin.getBridgeInstance().getPermissionsHandler();
    private static Database database = BridgePlugin.getBridgeInstance().getMongo();

    @Command(names = "grant", permissionNode = "bridge.permissions.grant", async = true)
    public static void guiGrant(Player player, @Parameter(name = "player") PermissionsUser user) {
        new GrantProcessMenu(new GrantProcess(permissionsHandler.getUser(player.getUniqueId()), user)).openMenu(player);
    }

    @Command(names = "ogrant", permissionNode = "bridge.permissions.grant", async = true)
    public static void commandGrant(CommandSender issuer,
                                    @Parameter(name = "player") PermissionsUser user,
                                    @Parameter(name = "group") PermissionsGroup group,
                                    @Parameter(name = "duration") String duration,
                                    @Parameter(name = "scope") String scope,
                                    @Parameter(name = "reason", wildcard = true) String reason) {
        if (TimeUtils.parseTime(duration) <= 0) {
            issuer.sendMessage(ChatColor.RED + "Duration '" + duration + "' is invalid.");
            return;
        }

        if (!RedstoneSharedAPI.serverExists(scope) && !scope.equalsIgnoreCase("all") && !scope.equalsIgnoreCase("global")) {
            issuer.sendMessage(ChatColor.RED + "Server '" + scope + "' does not exist.");
            return;
        }

        user.getGrants().add(new GrantBuilder((issuer instanceof Player) ? ((Player)issuer).getUniqueId():null)
                .permissionsGroup(group)
                .duration(TimeUtils.parseTime(duration))
                .scope(scope)
                .reason(reason)
                .get());


        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully granted '&r" + user.getColoredName() + "&a' the " + group.getFancyName() + " &agroup."));
//        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Duration: &r" + WordUtils.capitalize(duration)));
//        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Scope: &r" + (scope.equalsIgnoreCase("all") ? scope:RedstoneSharedAPI.getServer(scope).getServerID())));
//        issuer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reason: &r") + reason);
        //  TODO add an option for developers to toggle debug, then show the message above if they are in debug mode

        Player player = user.toPlayer();

        if (player != null) {
            user.update();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You now have access to the &r" + group.getFancyName() + " &7group."));
        }

        database.getPermissionsUserDAO().saveUser(user);
    }

    @Command(names = "grants", permissionNode = "bridge.permissions.grant.view", async = true)
    public static void viewGrants(Player player, @Parameter(name = "player") PermissionsUser user) {
        player.sendMessage(ChatColor.GOLD + "Viewing grants of " + user.getColoredName());
        new GrantDisplayMenu(user).openMenu(player);
    }

    @Command(names = "activegrant", permissionNode = "bridge.permissions.grant.active")
    public static void activeGrant(Player player, @Parameter(name = "player") PermissionsUser user) {
        player.sendMessage(ChatColor.YELLOW + "Active grant of '" + user.getActiveGrant().getPermissionsGroup().getFormattedName(user.getName()) + ChatColor.YELLOW + "' is:");
        user.getActiveGrant().show(player);
    }

    /*
        Debug commands
     */
    @Command(names = "cleargrants", permissionNode = "bridge.permissions.grant.clear", async = true)
    public static void clearGrants(Player player, @Parameter(name = "player") PermissionsUser user) {
        user.getGrants().clear();
        user.setActiveGrant(null);
        user.getActiveGrant();
        database.getPermissionsUserDAO().saveUser(user);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&dCleared grants of '%s'", user.getName())));
    }

}
