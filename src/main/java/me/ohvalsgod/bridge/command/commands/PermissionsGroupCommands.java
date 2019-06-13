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
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PermissionsGroupCommands {

    private static BridgePlugin plugin = BridgePlugin.getBridgeInstance();
    private static PermissionsHandler permissionsHandler = BridgePlugin.getBridgeInstance().getPermissionsHandler();
    private static Database database = BridgePlugin.getBridgeInstance().getMongo();

    private static Map<UUID, Callback<Player>> confirmation;
    private static ArrayList<String> help;

    static {
        confirmation = new HashMap<>();

        help = new ArrayList<>();
        help.add("&6&lPermissions Help &7- &fGroup commands");
        help.add("&e/group help &7- &fDisplays this help menu");
        help.add("&e/group list &7- &fDisplays a list of all groups");
        help.add("&e/group info <name> &7- &fDisplays a group's info");
        help.add("&e/group create <name> &7- &fCreate a group");
        help.add("&e/group delete <name> &7- &fDelete a group");
        help.add("&e/group add <name> <permission> &7- &fAdd a permission to a group");
        help.add("&e/group rem <name> <permission> &7- &fRemove a permission from a group");
        help.add("&e/group weight <name> <weight> &7- &fChange a group's weight");
        help.add("&e/group prefix <name> <prefix> &7- &fChange a group's prefix");
        help.add("&e/group toggleprefix <name> &7- &fToggle a group's prefix");
        help.add("&e/group suffix <name> <suffix> &7- &fChange a group's suffix");
        help.add("&e/group togglesuffix <name> &7- &fToggle a group's suffix");
        help.add("&e/group namecolor <name> <color> &7- &fChange a group's name color");
        help.add("&e/group default <name> &7- &fMake the group the default group");
        help.add("&e/group addinherit <name> <parent> &7- &fInherit permissions from parent group");
        help.add("&e/group delinherit <name> <parent> &7- &fRemoves parent from inherit group");
    }

    @Command(names = {"group", "group help"}, permissionNode = "bridge.group.help")
    public static void help(CommandSender sender) {
        help.forEach(s -> {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        });
    }

    @Command(names = "group list" , permissionNode = "bridge.group.list")
    public static void list(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&6All Bridge groups - &7Displaying &f%s &7groups", permissionsHandler.getPermissionsGroups().size())));

        List<PermissionsGroup> groups = new ArrayList<>(permissionsHandler.getPermissionsGroups().values());
        groups.sort(Comparator.comparingDouble(PermissionsGroup::getWeight));

        groups.forEach(group -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("%s &7- %s(Weight: %s)",
                        group.getFormattedName(group.getName()),
                        group.isDefaultGroup() ? "&7(Default: &atrue&7)" + " ":"",
                        group.getWeight()))));
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

    @Command(names = {"group add", "group addperm"}, permissionNode = "bridge.group.addpermission", async = true)
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

    @Command(names = {"group del", "group delperm"}, permissionNode = "bridge.group.removepermission", async = true)
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

    @Command(names = "group weight", permissionNode = "bridge.group.weight", async = true)
    public static void weight(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "weight") double weight) {
        if (group.getWeight() == weight) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat is already the current weight of the group."));
            return;
        }

        group.setWeight(weight);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eChanged weight of '%s&e' to &f%s&e.",
                        group.getFancyName(),
                        group.getWeight())));

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group prefix", permissionNode = "bridge.group.prefix", async = true)
    public static void prefix(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "prefix") String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eChanged prefix of '%s&e' from '%s&e' to '%s&e'.",
                        group.getFancyName(),
                        group.getPrefix(),
                        prefix)));
        group.setPrefix(prefix);

        if (!group.isShowPrefix()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have changed the prefix of the group, but it does not have show prefix enabled."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If you would like the prefix to be shown, use /group toggleprefix <group>"));
        } else {
            for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
                System.out.println(user.getActiveGrant() != null);
                if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                    user.update();
                }
            }
        }
    }

    @Command(names = "group toggleprefix", permissionNode = "bridge.group.prefix.toggle", async = true)
    public static void togglePrefix(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "state") boolean state) {
        group.setShowPrefix(state);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eToggled prefix of '%s&e' %s&e.",
                        group.getFancyName(),
                        (group.isShowPrefix() ? "§aon":"§coff"))));

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group suffix", permissionNode = "bridge.group.suffix", async = true)
    public static void suffix(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "suffix") String suffix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eChanged suffix of '%s&e' from '%s&e' to '%s&e'.",
                        group.getFancyName(),
                        group.getSuffix(),
                        suffix)));
        group.setSuffix(suffix);

        if (!group.isShowSuffix()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have changed the suffix of the group, but it does not have show suffix enabled."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If you would like the suffix to be shown, use /group togglesuffix <group>"));
        } else {
            for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
                System.out.println(user.getActiveGrant() != null);
                if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                    user.update();
                }
            }
        }
    }

    @Command(names = "group togglesuffix", permissionNode = "bridge.group.suffix.toggle", async = true)
    public static void toggleSuffix(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "state") boolean state) {
        group.setShowSuffix(state);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eToggled suffix of '%s&e' to %s&e.",
                        group.getFancyName(),
                        (group.isShowSuffix() ? "§aon":"§coff"))));

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group namecolor", permissionNode = "bridge.group.namecolor", async = true)
    public static void nameColor(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "namecolor") String namecolor) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&eChanged namecolor of '%s&e' from '%s&e' to '%s&e'.",
                        group.getFancyName(),
                        group.getFancyName(),
                        namecolor + group.getName())));
        group.setNameColor(namecolor);

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group default", permissionNode = "bridge.group.default", async = true)
    public static void changeDefault(CommandSender sender, @Parameter(name = "group") PermissionsGroup group) {
        PermissionsGroup lastDefault = permissionsHandler.getGroup(permissionsHandler.getDefaultGroupId());
        lastDefault.setDefaultGroup(false);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&e'%s&e' is no longer the default group.",
                        lastDefault.getFancyName())));
        group.setDefaultGroup(true);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&e'%s&e' is now the default group.",
                        group.getFancyName())));

        permissionsHandler.findDefaultGroup();

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group addinherit", permissionNode = "bridge.group.inherit.add", async = true)
    public static void addInherit(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "parent") PermissionsGroup parent) {

        if (group.getInherits().contains(parent.getUniqueId())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis group is already a parent of the selected group."));
            return;
        }

        if (parent.getInherits().contains(group.getUniqueId())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis group is a child of the parent group."));
            return;
        }

        group.getInherits().add(parent.getUniqueId());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&aAdded &e'%s&e' as a parent group of '%s&e.",
                        parent.getFancyName(),
                        group.getFancyName())));

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    @Command(names = "group delinherit", permissionNode = "bridge.group.inherit.add", async = true)
    public static void deleteInherit(CommandSender sender, @Parameter(name = "group") PermissionsGroup group, @Parameter(name = "parent") PermissionsGroup parent) {
        if (!group.getInherits().contains(parent.getUniqueId())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis group is not a child of the selected group."));
            return;
        }

        group.getInherits().remove(parent.getUniqueId());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&cRemoved &e'%s&e' from the parent groups of '%s&e.",
                        parent.getFancyName(),
                        group.getFancyName())));

        for (PermissionsUser user : database.getPermissionsUserDAO().getOnlineUsers()) {
            System.out.println(user.getActiveGrant() != null);
            if (user.getActiveGrant().getPermissionsGroupId().equalsIgnoreCase(group.getUniqueId())) {
                user.update();
            }
        }
    }

    /*
        DEBUG COMMANDS
     */

    @Command(names = "debugsave", async = true, permissionNode = "bridge.admin.debug.save")
    public static void debug(Player player) {
        long start = System.currentTimeMillis();
        database.getPermissionsUserDAO().getAllUsers().forEach(permissionsUser -> database.getPermissionsUserDAO().saveUser(permissionsUser));
        database.getPermissionsGroupDAO().getAllGroups().forEach(group -> database.getPermissionsGroupDAO().saveGroup(group));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "All permissions groups and users saved in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Command(names = "debuglistperms")
    public static void listPerms(Player player) {
        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            player.sendMessage((attachmentInfo.getValue() ? "§a":"§c") + attachmentInfo.getPermission());
        }
    }

}
