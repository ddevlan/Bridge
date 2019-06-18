package me.ohvalsgod.bridge.permissions;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PermissionListener implements Listener {

    private BridgePlugin plugin;
    private PermissionsHandler permissionsHandler;

    public PermissionListener(BridgePlugin plugin) {
        this.plugin = plugin;
        this.permissionsHandler = plugin.getPermissionsHandler();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PermissionsUser user;

        if (permissionsHandler.getUser(event.getPlayer().getUniqueId()) != null) {
            user = permissionsHandler.getUser(event.getPlayer().getUniqueId());
        } else {
            user = plugin.getMongo().getPermissionsUserDAO().getByUniqueId(event.getPlayer().getUniqueId());

            if (user == null) {
                user = permissionsHandler.createUser(event.getPlayer());
            }

            plugin.getMongo().getPermissionsUserDAO().saveUser(user);
        }

        permissionsHandler.getPermissionsUsers().put(user.getUUID(), user);
        user.setName(event.getPlayer().getName());
        user.update();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        PermissionsUser user = permissionsHandler.getUser(event.getPlayer().getUniqueId());
        event.setFormat(user.getActiveGrant().getPermissionsGroup().getFormattedName(event.getPlayer().getName()) + ChatColor.WHITE + ": " + event.getMessage());
    }

}
