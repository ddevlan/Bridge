package me.ohvalsgod.bridge.command.parameters;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bukkitlib.command.param.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsUserParameter implements ParameterType<PermissionsUser> {

    @Override
    public PermissionsUser transform(CommandSender sender, String source) {
        PermissionsUser user = BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(source);

        //TODO: create a new user, grab their uuid from mojang api
        if (user == null) {
            user = BridgePlugin.getBridgeInstance().getMongo().getPermissionsUserDAO().getByName(source);
        }

        if (user == null) {
            sender.sendMessage(ChatColor.RED + "No permissions user with the name '" + source + "' found.");
            return null;
        } else {
            BridgePlugin.getBridgeInstance().getPermissionsHandler().getPermissionsUsers().put(user.getUUID(), user);
        }

        return user;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Bukkit.getOnlinePlayers().stream().map(o -> ((Player) o).getName()).collect(Collectors.toList());
    }
}
