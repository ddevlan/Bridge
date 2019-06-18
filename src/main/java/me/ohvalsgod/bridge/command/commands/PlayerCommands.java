package me.ohvalsgod.bridge.command.commands;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bukkitlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerCommands {

    @Command(names = {"list", "who", "players", "online", "onlineplayers", "listplayers"}, async = true)
    public static void list(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getOrderedGroupsString()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getOrderedOnlinePlayersString()));
    }

    private static String getOrderedOnlinePlayersString() {
        List<String> players = new ArrayList<>();

        BridgePlugin.getBridgeInstance().getMongo().getPermissionsUserDAO().getOnlineUsers().stream().sorted(Comparator.comparingDouble(value -> value.getActiveGrant().getPermissionsGroup().getWeight())).forEach(user -> {
            players.add(user.getColoredName());
        });

        Collections.reverse(players);

        return String.format("(%s/%s) [%s&f]", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), String.join("&f, ", players));
    }

    private static String getOrderedGroupsString() {
        List<String> groups = new ArrayList<>();

        BridgePlugin.getBridgeInstance().getPermissionsHandler().getPermissionsGroups().values().stream().sorted(Comparator.comparingDouble(PermissionsGroup::getWeight)).forEach(group -> {
            groups.add(group.getFancyName());
        });

        Collections.reverse(groups);

        return String.join("&f, ", groups);
    }

}
