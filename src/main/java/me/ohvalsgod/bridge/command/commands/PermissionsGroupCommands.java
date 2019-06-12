package me.ohvalsgod.bridge.command.commands;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bukkitlib.command.Command;
import me.ohvalsgod.bukkitlib.command.param.Parameter;
import me.ohvalsgod.bukkitlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PermissionsGroupCommands {

    private static BridgePlugin plugin = BridgePlugin.getBridgeInstance();
    private static PermissionsHandler permissionsHandler = BridgePlugin.getBridgeInstance().getPermissionsHandler();
    private static Database database = BridgePlugin.getBridgeInstance().getMongo();

    private static Map<UUID, Callback<Player>> confirmation;

    public PermissionsGroupCommands(BridgePlugin plugin) {
        PermissionsGroupCommands.plugin = plugin;
        PermissionsGroupCommands.permissionsHandler = plugin.getPermissionsHandler();
        PermissionsGroupCommands.database = plugin.getMongo();
        confirmation = new HashMap<>();
    }

    @Command(names = {"group", "group help"}, permissionNode = "bridge.group.help")
    public static void help(CommandSender sender) {

    }

    @Command(names = "group create", permissionNode = "bridge.group.create", async = true)
    public static void create(CommandSender sender, @Parameter(name = "name") String name) {
        if (permissionsHandler.groupExists(name)) {
            sender.sendMessage(ChatColor.RED + "A group with the name '" + name + "' already exists.");
            return;
        }

        PermissionsGroup created = permissionsHandler.createGroup(name);
        database.getPermissionsGroupDAO().saveGroup(created);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eGroup with the name '%s&e' has been &acreated&e.", created.getFancyName())));
    }

    @Command(names = "group delete", permissionNode = "bridge.group.delete", async = true)
    public static void delete(Player player, @Parameter(name = "group") PermissionsGroup group) {
        if (!group.isDefaultGroup()) {
            if (confirmation.containsKey(player.getUniqueId())) {
                confirmation.get(player.getUniqueId()).callback(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&eAre you sure you want to &4delete &ethe '%s&e' group?", group.getFancyName())));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If you are sure, repeat the command again."));

                confirmation.put(player.getUniqueId(), (Callback<Player>) data -> {
                    //TODO: send redis update for all servers
                    database.getPermissionsGroupDAO().deleteGroup(group);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&eGroup with the name '%s&e' has been &4deleted&e.", group.getFancyName())
                    ));
                    permissionsHandler.getPermissionsGroups().remove(group.getUUID());
                    confirmation.remove(player.getUniqueId());

                    for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
                        System.out.println(user.getActiveGrant() != null);
                        if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                            database.getPermissionsUserDAO().saveUser(user);
                            user.update();

                            Player o = user.toPlayer();

                            o.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7The group in your active grant has been deleted."));
                            o.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&oYou have been reset to the default group."));
                        }
                    }
                });

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        confirmation.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Group deletion confirmation expired."));
                    }
                }.runTaskLater(plugin, 20 * 60);
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlease set another group to the default group, then attempt to delete again."));
        }
    }

    @Command(names = {"group add", "group addpermission"}, permissionNode = "bridge.group.addpermission", async = true)
    public static void addPermission(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "permission") String permission) {
        if (group.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&oGroup already has the permission, update suppressed."));
        } else {
            group.addPermission(permission);
            database.getPermissionsGroupDAO().saveGroup(group);

            for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
                System.out.println(user.getActiveGrant() != null);
                if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                    user.update();
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&aAdded &ethe '&f%s&e' permission to the '%s&e' group.", permission, group.getFancyName())));
        }
    }

    @Command(names = {"group rem", "group removepermission"}, permissionNode = "bridge.group.removepermission", async = true)
    public static void delPermission(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "permission") String permission) {
        if (!group.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&oGroup doesn't have the permission, update suppressed."));
        } else {
            group.removePermission(permission);
            database.getPermissionsGroupDAO().saveGroup(group);

            for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
                System.out.println(user.getActiveGrant() != null);
                if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                    user.update();
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&cRemoved &ethe '&f%s&e' permission to the '%s&e' group.", permission, group.getFancyName())));
        }
    }

    @Command(names = "group info", permissionNode = "bridge.group.info")
    public static void info(CommandSender sender, @Parameter(name = "group") PermissionsGroup group) {
        List<String> info = new ArrayList<>();

        info.add("&7&m------------------------------");
        info.add(String.format("&7[DEBUG] &6UUID: &f%s", group.getUniqueId()));
        info.add(String.format("&6Information on '%s&6'", group.getFancyName()));
        info.add(String.format("&6Example display: %s&f: hello!", group.getFormattedName(sender.getName())));
        info.add(" ");
        info.add(String.format("&eDefault: &f%s", (group.isDefaultGroup() ? "&atrue":"&cfalse")));
        info.add(String.format("&eWeight: &f%s", group.getWeight()));
        info.add(" ");

        if (!group.getPrefix().isEmpty()) {
            info.add(String.format("&ePrefix: &f%s &7(%s&7)", group.getPrefix(), (group.isShowPrefix() ? "&aenabled":"&cdisabled")));
        } else {
            info.add("&7Prefix is empty.");
        }

        if (!group.getSuffix().isEmpty()) {
            info.add(String.format("&eSuffix: &f%s &7(%s&7)", group.getSuffix(), (group.isShowSuffix() ? "&aenabled":"&cdisabled")));
            info.add(" ");
        } else {
            info.add("&7Suffix is empty.");
        }

        info.add(" ");

        if (!group.getPermissions().isEmpty()) {
            group.getPermissions().forEach(s -> info.add(String.format(" &7- &f%s", s)));
        } else {
            info.add("&7This group has no permissions.");
        }

        info.add(" ");

        if (!group.getInherits().isEmpty()) {
            group.getInherits().forEach(s -> info.add(String.format(" &7- &f%s", s)));
        } else {
            info.add("&7This group has no parents.");
        }

        info.add("&7&m------------------------------");

        info.forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
    }

    @Command(names = "debugsave")
    public static void debug(Player player) {
        long start = System.currentTimeMillis();
        database.getPermissionsUserDAO().getAllUsers().forEach(permissionsUser -> database.getPermissionsUserDAO().saveUser(permissionsUser));
        database.getPermissionsGroupDAO().getAllGroups().forEach(group -> database.getPermissionsGroupDAO().saveGroup(group));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "All permissions groups and users saved in " + (System.currentTimeMillis() - start) + "ms.");
    }

}
