package me.ohvalsgod.bridge.command.parameters;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bukkitlib.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsGroupParameter implements ParameterType<PermissionsGroup> {

    @Override
    public PermissionsGroup transform(CommandSender sender, String source) {
        PermissionsGroup group = BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(source);

        if (group == null) {
            sender.sendMessage(ChatColor.RED + "No permissions group with the name '" + source + "' found.");
            return null;
        }

        if (!sender.hasPermission("bridge.permissions.groups." + source)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return null;
        }

        return group;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (PermissionsGroup group : BridgePlugin.getBridgeInstance().getPermissionsHandler().getPermissionsGroups().values().stream().sorted(Comparator.comparingDouble(PermissionsGroup::getWeight)).collect(Collectors.toList())) {
            if (sender.hasPermission("bridge.permissions.groups." + group.getName().toLowerCase())) {
                completions.add(group.getName());
            }
        }

        return completions;
    }
}
